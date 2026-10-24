package com.fnspirits.entity;

import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModEntities;
import com.fnspirits.spirit.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** "Extrae" un espiritu del santuario/caja: sale como entidad con efectos y sonido. */
public final class SpiritSpawner {
    private SpiritSpawner() {}

    public static SpiritEntity release(ServerLevel sl, Vec3 pos, @Nullable Player player) {
        int hackerLevel = 0;
        if (player != null && player.getOffhandItem().getItem() instanceof SpiritItem si && si.type() == SpiritType.HACKER) {
            hackerLevel = SpiritData.getLevel(player.getOffhandItem());
        }
        SpiritType t = SpiritRarity.rollType(sl.random, hackerLevel);
        SpiritVariant v = SpiritRarity.rollVariant(sl.random, hackerLevel);

        SpiritEntity e = new SpiritEntity(ModEntities.SPIRIT.get(), sl);
        e.setSpirit(t, v);
        e.moveTo(pos.x, pos.y, pos.z, sl.random.nextFloat() * 360f, 0f);
        e.setDeltaMovement(0, 0.3, 0);
        sl.addFreshEntity(e);

        sl.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.3, pos.z, 30, 0.4, 0.4, 0.4, 0.08);
        if (v == SpiritVariant.GOLD) sl.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y + 0.3, pos.z, 25, 0.4, 0.5, 0.4, 0.2);
        if (v == SpiritVariant.GREEN) sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y + 0.3, pos.z, 15, 0.4, 0.4, 0.4, 0.05);
        BlockPos bp = BlockPos.containing(pos);
        sl.playSound(null, bp, SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 1f, 1.5f);
        sl.playSound(null, bp, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.NEUTRAL, 1f, 1f);
        return e;
    }
}
