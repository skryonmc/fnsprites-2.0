package com.fnspirits.worldgen;

import com.fnspirits.block.ArrowPadBlock;
import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Zona de codigo: plataforma oscura con un terminal al centro y 4 paneles de flechas en fila. */
public class CodeZoneFeature extends Feature<NoneFeatureConfiguration> {
    public CodeZoneFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel l = ctx.level();
        BlockPos o = ctx.origin();
        RandomSource r = ctx.random();

        if (!l.getBlockState(o.below()).isSolid() || !l.getFluidState(o).isEmpty() || !l.getFluidState(o.below()).isEmpty()) return false;
        for (int[] c : new int[][]{{-4, -3}, {4, -3}, {-4, 3}, {4, 3}}) {
            int x = o.getX() + c[0], z = o.getZ() + c[1];
            int h = l.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (Math.abs(h - o.getY()) > 2) return false;
            if (!l.getBlockState(new BlockPos(x, h - 1, z)).getFluidState().isEmpty()) return false;
        }

        int by = o.getY() - 1;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos p = new BlockPos(o.getX() + dx, by, o.getZ() + dz);
                BlockState floor = r.nextInt(4) == 0 ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                        : Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
                l.setBlock(p, floor, 2);
                for (int k = 1; k <= 6; k++) {
                    BlockPos q = p.below(k);
                    if (l.getBlockState(q).isSolid()) break;
                    l.setBlock(q, Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }
                for (int y = 0; y <= 4; y++) {
                    BlockPos q = p.above(1 + y);
                    if (!l.getBlockState(q).isAir()) l.setBlock(q, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
        // esquinas con luz
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sz = -1; sz <= 1; sz += 2) {
                BlockPos b = new BlockPos(o.getX() + 4 * sx, o.getY(), o.getZ() + 3 * sz);
                l.setBlock(b, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2);
                l.setBlock(b.above(), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2);
                l.setBlock(b.above(2), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            }
        }
        l.setBlock(o, ModBlocks.CODE_TERMINAL.get().defaultBlockState(), 2);
        int[] xs = {-3, -1, 1, 3};
        for (int i = 0; i < 4; i++) {
            l.setBlock(new BlockPos(o.getX() + xs[i], o.getY(), o.getZ() + 2),
                    ModBlocks.ARROW_PAD.get().defaultBlockState().setValue(ArrowPadBlock.ARROW, i), 2);
        }
        return true;
    }
}
