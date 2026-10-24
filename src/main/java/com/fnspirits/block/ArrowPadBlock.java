package com.fnspirits.block;

import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Panel de flecha. arrow: 0=arriba 1=derecha 2=abajo 3=izquierda. Al pulsarlo introduce esa flecha en el terminal cercano. */
public class ArrowPadBlock extends Block {
    public static final IntegerProperty ARROW = IntegerProperty.create("arrow", 0, 3);
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 2, 15);

    public ArrowPadBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2f, 6f)
                .requiresCorrectToolForDrops().noOcclusion().sound(SoundType.DEEPSLATE_TILES).lightLevel(s -> 5));
        registerDefaultState(stateDefinition.any().setValue(ARROW, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) { b.add(ARROW); }
    @Override public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) { return SHAPE; }

    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        int a = switch (ctx.getHorizontalDirection()) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            default -> 3;
        };
        return defaultBlockState().setValue(ARROW, a);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer sp)) return InteractionResult.CONSUME;
        CodeTerminalBlockEntity term = findTerminal(level, pos);
        if (term == null) sp.displayClientMessage(Component.translatable("msg.fnspirits.code_none"), true);
        else term.input(state.getValue(ARROW), sp);
        return InteractionResult.CONSUME;
    }

    private static CodeTerminalBlockEntity findTerminal(Level level, BlockPos pos) {
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-8, -3, -8), pos.offset(8, 3, 8))) {
            if (level.getBlockState(p).is(ModBlocks.CODE_TERMINAL.get()) && level.getBlockEntity(p) instanceof CodeTerminalBlockEntity t) return t;
        }
        return null;
    }
}
