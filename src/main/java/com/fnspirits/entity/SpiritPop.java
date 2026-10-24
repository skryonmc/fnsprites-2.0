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

/** Hace que un espiritu "salte" (de un cofre, un mob, un terminal...) con efectos y sonido segun su variante. */
public final class SpiritPop {
    private SpiritPop() {}

    public static int hackerLevel(@Nullable Player p) {
        if (p != null && p.getOffhandItem().getItem() instanceof SpiritItem si && si.type() == SpiritType.HACKER) {
            return SpiritData.getLevel(p.getOffhandItem());
        }
        return 0;
    }

    /** bonus: sube la rareza (zonas de codigo y jefes). El Hacker equipado tambien suma. */
    public static SpiritEntity rollAndSpawn(ServerLevel sl, Vec3 pos, @Nullable Player p, int bonus, boolean fancy) {
        int hl = hackerLevel(p) + bonus;
        return spawn(sl, pos, SpiritRarity.rollType(sl.random, hl), SpiritRarity.rollVariant(sl.random, hl), fancy);
    }

    public static SpiritEntity spawn(ServerLevel sl, Vec3 pos, SpiritType t, SpiritVariant v, boolean fancy) {
        SpiritEntity e = new SpiritEntity(ModEntities.SPIRIT.get(), sl);
        e.setSpirit(t, v);
        e.setWild(true);
        e.moveTo(pos.x, pos.y, pos.z, sl.random.nextFloat() * 360f, 0f);
        e.setDeltaMovement((sl.random.nextDouble() - 0.5) * 0.1, 0.35, (sl.random.nextDouble() - 0.5) * 0.1);
        sl.addFreshEntity(e);

        BlockPos bp = BlockPos.containing(pos);
        float pitch = 0.9f + t.ordinal() * 0.07f;
        sl.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.2, pos.z, fancy ? 25 : 10, 0.35, 0.35, 0.35, 0.08);
        sl.playSound(null, bp, SoundEvents.ALLAY_ITEM_THROWN, SoundSource.NEUTRAL, 1f, pitch);
        if (v == SpiritVariant.GREEN) {
            sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y + 0.3, pos.z, 18, 0.4, 0.4, 0.4, 0.05);
            sl.playSound(null, bp, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 1f, 1.4f);
        } else if (v == SpiritVariant.GOLD) {
            sl.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y + 0.3, pos.z, 30, 0.4, 0.5, 0.4, 0.25);
            sl.sendParticles(ParticleTypes.FIREWORK, pos.x, pos.y + 0.5, pos.z, 20, 0.3, 0.6, 0.3, 0.12);
            sl.playSound(null, bp, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.NEUTRAL, 0.8f, 1.2f);
        }
        return e;
    }
}
