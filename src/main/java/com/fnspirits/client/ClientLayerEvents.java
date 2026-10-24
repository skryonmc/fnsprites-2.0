package com.fnspirits.client;

import com.fnspirits.SpiritsMod;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientLayerEvents {

    @Mod.EventBusSubscriber(modid = SpiritsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers e) {
            for (String skin : e.getSkins()) {
                PlayerRenderer r = e.getSkin(skin);
                if (r != null) r.addLayer(new BackSpiritLayer(r));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SpiritsMod.MODID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onUnload(LevelEvent.Unload e) {
            if (e.getLevel().isClientSide()) BackSpiritLayer.clearCache();
        }
    }
}
