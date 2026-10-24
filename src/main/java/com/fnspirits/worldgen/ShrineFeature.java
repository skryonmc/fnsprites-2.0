package com.fnspirits.worldgen;

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

/** Santuario: plataforma de piedra con 4 cajas de extraccion, un pedestal central y 4 pilares con faroles. */
public class ShrineFeature extends Feature<NoneFeatureConfiguration> {
    public ShrineFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel l = ctx.level();
        BlockPos o = ctx.origin();
        RandomSource r = ctx.random();

        // terreno razonablemente plano y sin agua
        if (!l.getBlockState(o.below()).isSolid() || !l.getFluidState(o).isEmpty() || !l.getFluidState(o.below()).isEmpty()) return false;
        for (int[] c : new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}}) {
            int x = o.getX() + c[0], z = o.getZ() + c[1];
            int h = l.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (Math.abs(h - o.getY()) > 2) return false;
            if (!l.getBlockState(new BlockPos(x, h - 1, z)).getFluidState().isEmpty()) return false;
        }

        int by = o.getY() - 1;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > 6) continue;
                BlockPos p = new BlockPos(o.getX() + dx, by, o.getZ() + dz);
                int roll = r.nextInt(6);
                BlockState floor = roll == 0 ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState()
                        : roll == 1 ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState()
                        : Blocks.STONE_BRICKS.defaultBlockState();
                l.setBlock(p, floor, 2);
                for (int k = 1; k <= 6; k++) { // rellena huecos debajo
                    BlockPos q = p.below(k);
                    if (l.getBlockState(q).isSolid()) break;
                    l.setBlock(q, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                }
                for (int y = 0; y <= 5; y++) { // despeja arriba
                    BlockPos q = p.above(1 + y);
                    if (!l.getBlockState(q).isAir()) l.setBlock(q, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
        // adorno de amatista
        for (int[] c : new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}}) {
            l.setBlock(new BlockPos(o.getX() + c[0], by, o.getZ() + c[1]), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        }
        // pilares con farol
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sz = -1; sz <= 1; sz += 2) {
                BlockPos b = new BlockPos(o.getX() + 3 * sx, o.getY(), o.getZ() + 3 * sz);
                for (int y = 0; y < 3; y++) l.setBlock(b.above(y), Blocks.STONE_BRICKS.defaultBlockState(), 2);
                l.setBlock(b.above(3), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
                l.setBlock(b.above(4), Blocks.LANTERN.defaultBlockState(), 2);
            }
        }
        // cajas de extraccion + pedestal central
        BlockState box = ModBlocks.EXTRACTION_BOX.get().defaultBlockState();
        for (int[] c : new int[][]{{-2, 0}, {2, 0}, {0, -2}, {0, 2}}) {
            l.setBlock(new BlockPos(o.getX() + c[0], o.getY(), o.getZ() + c[1]), box, 2);
        }
        l.setBlock(o, ModBlocks.PEDESTAL.get().defaultBlockState(), 2);
        return true;
    }
}
