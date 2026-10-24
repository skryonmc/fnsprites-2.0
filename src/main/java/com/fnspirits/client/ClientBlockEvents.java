package com.fnspirits.client;

import com.fnspirits.SpiritsMod;
import com.fnspirits.registry.ModBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpiritsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBlockEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerBlockEntityRenderer(ModBlocks.PEDESTAL_BE.get(), PedestalRenderer::new);
    }
}
