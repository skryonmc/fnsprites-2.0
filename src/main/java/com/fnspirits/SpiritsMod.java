package com.fnspirits;

import com.fnspirits.loot.ModLoot;
import com.fnspirits.network.ModNetwork;
import com.fnspirits.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpiritsMod.MODID)
public class SpiritsMod {
    public static final String MODID = "fnspirits";

    public SpiritsMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModBlocks.BLOCK_ENTITIES.register(bus);
        ModItems.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModParticles.PARTICLES.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModLoot.SERIALIZERS.register(bus);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(ModNetwork::register);
    }
}
