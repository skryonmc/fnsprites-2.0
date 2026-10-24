package com.fnspirits.client;

import com.fnspirits.SpiritsMod;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.network.DoubleJumpPacket;
import com.fnspirits.network.ModNetwork;
import com.fnspirits.registry.ModParticles;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpiritsMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean wasJumpDown = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        ClientLevel lvl = mc.level;
        LocalPlayer me = mc.player;
        if (lvl == null || me == null) return;

        handleDoubleJump(mc, me);
        if (mc.isPaused()) return;

        long t = lvl.getGameTime();
        // Efectos de variante en jugadores con espiritu equipado (en la espalda)
        if (t % 2 == 0) {
            for (Player p : lvl.players()) {
                if (p.distanceToSqr(me) > 48 * 48) continue;
                ItemStack s = p.getOffhandItem();
                if (!(s.getItem() instanceof SpiritItem si)) continue;
                double yaw = Math.toRadians(p.yBodyRot);
                double bx = p.getX() + Math.sin(yaw) * 0.35;
                double bz = p.getZ() - Math.cos(yaw) * 0.35;
                effect(lvl, bx, p.getY() + 1.1, bz, si.variant(), t, SpiritData.getLevel(s));
            }
        }
        // Efectos en espiritus tirados en el suelo
        if (t % 4 == 0) {
            for (ItemEntity ie : lvl.getEntitiesOfClass(ItemEntity.class, me.getBoundingBox().inflate(24))) {
                ItemStack s = ie.getItem();
                if (s.getItem() instanceof SpiritItem si) {
                    effect(lvl, ie.getX(), ie.getY() + 0.3, ie.getZ(), si.variant(), t, SpiritData.getLevel(s));
                }
            }
        }
    }

    /** Public para reutilizarlo con entidades errantes (Parte 3). */
    public static void effect(ClientLevel lvl, double x, double y, double z, SpiritVariant v, long t, int level) {
        RandomSource r = lvl.random;
        if (v == SpiritVariant.GREEN) {
            int n = level >= 5 ? 2 : 1;
            for (int i = 0; i < n; i++) {
                lvl.addParticle(ModParticles.DIGIT.get(),
                        x + (r.nextDouble() - 0.5) * 0.5, y + r.nextDouble() * 0.4, z + (r.nextDouble() - 0.5) * 0.5, 0, 0, 0);
            }
        } else if (v == SpiritVariant.GOLD) {
            double a = t * 0.25 + r.nextDouble() * 0.6;
            double rad = 0.35;
            lvl.addParticle(ModParticles.SHINE.get(),
                    x + Math.cos(a) * rad, y + Math.sin(t * 0.1) * 0.2, z + Math.sin(a) * rad,
                    -Math.sin(a) * 0.04, 0.01, Math.cos(a) * 0.04);
        }
    }

    private static void handleDoubleJump(Minecraft mc, LocalPlayer p) {
        boolean down = mc.options.keyJump.isDown();
        boolean pressed = down && !wasJumpDown;
        wasJumpDown = down;
        if (!pressed || p.onGround() || p.isInWater() || p.onClimbable() || p.getAbilities().flying || p.isFallFlying()) return;
        ItemStack s = p.getOffhandItem();
        if (s.getItem() instanceof SpiritItem si && si.type() == SpiritType.JACK_RABBIT) {
            ModNetwork.CHANNEL.sendToServer(new DoubleJumpPacket());
        }
    }
}
