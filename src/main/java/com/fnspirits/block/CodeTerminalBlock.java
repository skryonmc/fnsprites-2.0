package com.fnspirits.block;

import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Terminal de codigo: clic derecho muestra 3 s un codigo de 4 flechas; hay que pulsar los paneles en ese orden. */
public class CodeTerminalBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 13, 13);

    public CodeTerminalBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3f, 8f)
                .requiresCorrectToolForDrops().noOcclusion().sound(SoundType.DEEPSLATE_TILES).lightLevel(s -> 7));
    }

    @Override public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) { return SHAPE; }
    @Override public RenderShape getRenderShape(BlockState s) { return RenderShape.MODEL; }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CodeTerminalBlockEntity(pos, state); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer sp)) return InteractionResult.CONSUME;
        if (level.getBlockEntity(pos) instanceof CodeTerminalBlockEntity be) {
            if (be.isLocked()) {
                sp.displayClientMessage(Component.translatable("msg.fnspirits.code_locked"), true);
            } else {
                be.resetProgress();
                sp.displayClientMessage(Component.translatable("msg.fnspirits.code_show", be.codeString()), true);
            }
        }
        return InteractionResult.CONSUME;
    }
}
