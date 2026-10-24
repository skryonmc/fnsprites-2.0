package com.fnspirits.loot;

import com.fnspirits.SpiritsMod;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLoot {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SpiritsMod.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> HACKER_MINING =
            SERIALIZERS.register("hacker_mining", HackerMiningModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SPIRIT_CHEST =
            SERIALIZERS.register("spirit_chest", SpiritChestModifier.CODEC);

    private ModLoot() {}
}
