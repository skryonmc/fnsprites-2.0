package com.fnspirits.client;

import com.fnspirits.SpiritsMod;
import com.fnspirits.client.particle.DigitParticle;
import com.fnspirits.client.particle.ShineParticle;
import com.fnspirits.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpiritsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent e) {
        e.registerSpriteSet(ModParticles.DIGIT.get(), DigitParticle.Provider::new);
        e.registerSpriteSet(ModParticles.SHINE.get(), ShineParticle.Provider::new);
    }
}
