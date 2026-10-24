package com.fnspirits.block;

import com.fnspirits.entity.SpiritPop;
import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class CodeTerminalBlockEntity extends BlockEntity {
    public static final int LOCK_TICKS = 48000; // 40 min
    private static final String[] ARROWS = {"\u2191", "\u2192", "\u2193", "\u2190"};

    private int[] code = new int[4];
    private int progress = 0;
    private long lockedUntil = 0;

    public CodeTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CODE_TERMINAL_BE.get(), pos, state);
        newCode();
    }

    private void newCode() {
        Random r = new Random();
        for (int i = 0; i < 4; i++) code[i] = r.nextInt(4);
    }

    public boolean isLocked() { return level != null && level.getGameTime() < lockedUntil; }
    public void resetProgress() { progress = 0; setChanged(); }

    public String codeString() {
        StringBuilder sb = new StringBuilder();
        for (int c : code) sb.append(ARROWS[c]).append(' ');
        return sb.toString().trim();
    }

    public void input(int value, ServerPlayer p) {
        if (!(level instanceof ServerLevel sl)) return;
        if (isLocked()) {
            p.displayClientMessage(Component.translatable("msg.fnspirits.code_locked"), true);
            return;
        }
        Vec3 c = Vec3.atCenterOf(worldPosition);
        if (value == code[progress]) {
            progress++;
            sl.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 1f, 0.7f + progress * 0.25f);
            sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, c.x, c.y + 0.9, c.z, 6, 0.3, 0.2, 0.3, 0);
            if (progress >= 4) {
                progress = 0;
                lockedUntil = sl.getGameTime() + LOCK_TICKS;
                newCode();
                p.displayClientMessage(Component.translatable("msg.fnspirits.code_ok"), true);
                // zona de codigo: rareza mejorada (+2), mas aun con Hacker
                SpiritPop.rollAndSpawn(sl, c.add(0, 1.3, 0), p, 2, true);
            } else {
                p.displayClientMessage(Component.translatable("msg.fnspirits.code_progress", progress, 4), true);
            }
        } else {
            progress = 0;
            sl.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.5f);
            sl.sendParticles(ParticleTypes.SMOKE, c.x, c.y + 0.9, c.z, 10, 0.3, 0.2, 0.3, 0.02);
            p.displayClientMessage(Component.translatable("msg.fnspirits.code_wrong"), true);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray("Code", code);
        tag.putInt("Progress", progress);
        tag.putLong("LockedUntil", lockedUntil);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        int[] c = tag.getIntArray("Code");
        if (c.length == 4) code = c;
        progress = tag.getInt("Progress");
        lockedUntil = tag.getLong("LockedUntil");
    }
}
