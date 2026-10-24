package com.fnspirits.block;

import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExtractionBoxBlockEntity extends BlockEntity {
    /** Tick de juego en que vuelve a estar cargada. 0 = cargada desde el inicio. */
    public long readyAt = 0;

    public ExtractionBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.EXTRACTION_BOX_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong("ReadyAt", readyAt);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readyAt = tag.getLong("ReadyAt");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExtractionBoxBlockEntity be) {
        if (level.getGameTime() % 20 == 0 && !state.getValue(ExtractionBoxBlock.CHARGED) && level.getGameTime() >= be.readyAt) {
            level.setBlock(pos, state.setValue(ExtractionBoxBlock.CHARGED, true), 3);
        }
    }
}
