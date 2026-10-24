package com.fnspirits.ability;

import com.fnspirits.SpiritsMod;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpiritsMod.MODID)
public class JackRabbitHandler {
    private static final String FLAG = "fnspirits_dj";

    /** Al tocar el suelo se recupera el doble salto. */
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player instanceof ServerPlayer p && p.onGround()) {
            p.getPersistentData().putBoolean(FLAG, false);
        }
    }

    public static void tryDoubleJump(ServerPlayer p) {
        ItemStack s = p.getOffhandItem();
        if (!(s.getItem() instanceof SpiritItem i) || i.type() != SpiritType.JACK_RABBIT) return;
        CompoundTag pd = p.getPersistentData();
        if (p.onGround() || p.isInWater() || p.getAbilities().flying || pd.getBoolean(FLAG)) return;
        if (!SpiritData.ready(s, p.level())) return;

        int lvl = SpiritData.getLevel(s);
        Vec3 m = p.getDeltaMovement();
        p.setDeltaMovement(m.x, 0.42 + 0.03 * lvl, m.z);
        p.hurtMarked = true; // sincroniza el movimiento con el cliente
        p.fallDistance = 0;
        pd.putBoolean(FLAG, true);
        SpiritData.startCooldown(s, p.level());

        p.level().playSound(null, p.blockPosition(), SoundEvents.RABBIT_JUMP, SoundSource.PLAYERS, 1f, 1.4f);
        if (p.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CLOUD, p.getX(), p.getY(), p.getZ(), 8, 0.25, 0.05, 0.25, 0.02);
        }
    }
}
