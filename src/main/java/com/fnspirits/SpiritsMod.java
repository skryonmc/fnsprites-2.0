package com.fnspirits;

import com.fnspirits.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpiritsMod.MODID)
public class SpiritsMod {
    public static final String MODID = "fnspirits";

    public SpiritsMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(bus);
    }
}
