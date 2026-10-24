package com.fnspirits.registry;

import com.fnspirits.SpiritsMod;
import com.fnspirits.entity.SpiritEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SpiritsMod.MODID);

    /** Una sola entidad; el tipo y la variante van en datos sincronizados. */
    public static final RegistryObject<EntityType<SpiritEntity>> SPIRIT = ENTITIES.register("spirit", () ->
            EntityType.Builder.of(SpiritEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.9f).clientTrackingRange(8).build("spirit"));

    private ModEntities() {}
}
