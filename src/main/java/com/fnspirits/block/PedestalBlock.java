package com.fnspirits.block;

import com.fnspirits.item.SpiritItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

/** Pedestal de exhibicion: pon un espiritu encima y se ve con su modelo 3D animado girando. */
public class PedestalBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 12, 13);

    public PedestalBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5f, 6f)
                .requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE));
    }

    @Override public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) { return SHAPE; }
    @Override public RenderShape getRenderShape(BlockState s) { return RenderShape.MODEL; }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PedestalBlockEntity(pos, state); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof PedestalBlockEntity ped)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.CONSUME;

        ItemStack held = player.getItemInHand(hand);
        ItemStack current = ped.getItem();
        boolean holdingSpirit = held.getItem() instanceof SpiritItem;

        if (!current.isEmpty()) {
            if (!player.getInventory().add(current)) player.drop(current, false);
            ped.setItem(ItemStack.EMPTY);
        }
        if (holdingSpirit) {
            ped.setItem(held.split(1));
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1f, 1.2f);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PedestalBlockEntity ped) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ped.getItem());
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}
