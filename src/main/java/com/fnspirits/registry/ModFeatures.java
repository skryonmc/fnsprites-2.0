package com.fnspirits.registry;

import com.fnspirits.SpiritsMod;
import com.fnspirits.worldgen.ShrineFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, SpiritsMod.MODID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SHRINE = FEATURES.register("shrine", ShrineFeature::new);

    private ModFeatures() {}
}
