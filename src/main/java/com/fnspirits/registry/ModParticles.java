package com.fnspirits.registry;

import com.fnspirits.SpiritsMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.PARTICLE_TYPES, SpiritsMod.MODID);

    /** Verdes: numeros que caen estilo codigo. */
    public static final RegistryObject<SimpleParticleType> DIGIT = PARTICLES.register("digit", () -> new SimpleParticleType(true));
    /** Dorados: destello que orbita. */
    public static final RegistryObject<SimpleParticleType> SHINE = PARTICLES.register("shine", () -> new SimpleParticleType(true));

    private ModParticles() {}
}
