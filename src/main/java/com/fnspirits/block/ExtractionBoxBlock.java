package com.fnspirits.block;

import com.fnspirits.entity.SpiritSpawner;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModBlocks;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritRewards;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Caja de extraccion del santuario.
 *  - Cargada (brilla): clic derecho con la mano vacia -> sale un espiritu. Luego se recarga (15 min).
 *  - Con un espiritu en la mano: clic derecho dos veces seguidas -> lo EXTRAE (se consume, da XP;
 *    Corona nivel 5 da la siguiente de la cadena).
 */
public class ExtractionBoxBlock extends BaseEntityBlock {
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final int COOLDOWN_TICKS = 18000;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public ExtractionBoxBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5f, 8f)
                .requiresCorrectToolForDrops().noOcclusion().sound(SoundType.AMETHYST)
                .lightLevel(s -> s.getValue(CHARGED) ? 10 : 3));
        registerDefaultState(stateDefinition.any().setValue(CHARGED, true));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) { b.add(CHARGED); }
    @Override public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) { return SHAPE; }
    @Override public RenderShape getRenderShape(BlockState s) { return RenderShape.MODEL; }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ExtractionBoxBlockEntity(pos, state); }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlocks.EXTRACTION_BOX_BE.get(), ExtractionBoxBlockEntity::tick);
    }

    /** Cajas colocadas por jugadores empiezan descargadas (evita duplicar espiritus rompiendo y poniendo). */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof ExtractionBoxBlockEntity be) {
            be.readyAt = level.getGameTime() + COOLDOWN_TICKS;
            be.setChanged();
        }
        level.setBlock(pos, state.setValue(CHARGED, false), 3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer sp)) return InteractionResult.CONSUME;
        ItemStack held = player.getItemInHand(hand);

        // ---- extraer un espiritu que llevas (confirmacion con doble clic) ----
        if (held.getItem() instanceof SpiritItem) {
            var pd = player.getPersistentData();
            long now = level.getGameTime();
            boolean confirmed = pd.getLong("fnspirits_confirm_pos") == pos.asLong() && now - pd.getLong("fnspirits_confirm_t") < 100;
            if (!confirmed) {
                pd.putLong("fnspirits_confirm_pos", pos.asLong());
                pd.putLong("fnspirits_confirm_t", now);
                sp.displayClientMessage(Component.translatable("msg.fnspirits.extract_hint"), true);
                return InteractionResult.CONSUME;
            }
            pd.putLong("fnspirits_confirm_t", 0);
            int lvl = SpiritData.getLevel(held);
            SpiritRewards.onExtract(sp, held);
            sp.giveExperiencePoints(lvl * 15);
            held.shrink(1);
            ServerLevel sl = (ServerLevel) level;
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 25, 0.3, 0.4, 0.3, 0.05);
            level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1f, 1.3f);
            sp.displayClientMessage(Component.translatable("msg.fnspirits.extracted"), true);
            return InteractionResult.CONSUME;
        }

        // ---- sacar un espiritu de la caja ----
        if (level.getBlockEntity(pos) instanceof ExtractionBoxBlockEntity box) {
            if (state.getValue(CHARGED)) {
                SpiritSpawner.release((ServerLevel) level, Vec3.atCenterOf(pos).add(0, 0.9, 0), player);
                box.readyAt = level.getGameTime() + COOLDOWN_TICKS;
                box.setChanged();
                level.setBlock(pos, state.setValue(CHARGED, false), 3);
            } else {
                long left = Math.max(0, box.readyAt - level.getGameTime());
                sp.displayClientMessage(Component.translatable("msg.fnspirits.box_charging", left / 1200 + 1), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
        if (state.getValue(CHARGED) && r.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                    (r.nextDouble() - 0.5) * 0.6, 0.4, (r.nextDouble() - 0.5) * 0.6);
        }
    }
}
