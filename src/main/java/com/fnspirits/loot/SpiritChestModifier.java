package com.fnspirits.loot;

import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModItems;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritRarity;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

/** Agrega un espiritu al botin de cofres con poca probabilidad. Hacker mejora probabilidad y rareza. */
public class SpiritChestModifier extends LootModifier {
    public static final Supplier<Codec<SpiritChestModifier>> CODEC =
            Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SpiritChestModifier::new)));

    public SpiritChestModifier(LootItemCondition[] conditions) { super(conditions); }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext ctx) {
        if (!ctx.getQueriedLootTableId().getPath().startsWith("chests/")) return loot;

        int hackerLevel = 0;
        Entity e = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (e instanceof Player p && p.getOffhandItem().getItem() instanceof SpiritItem si && si.type() == SpiritType.HACKER) {
            hackerLevel = SpiritData.getLevel(p.getOffhandItem());
        }
        var r = ctx.getRandom();
        if (r.nextDouble() >= 0.06 * (1 + 0.2 * hackerLevel)) return loot;

        SpiritType t = SpiritRarity.rollType(r, hackerLevel);
        SpiritVariant v = SpiritRarity.rollVariant(r, hackerLevel);
        loot.add(new ItemStack(ModItems.get(t, v)));
        return loot;
    }

    @Override
    public Codec<? extends net.minecraftforge.common.loot.IGlobalLootModifier> codec() { return CODEC.get(); }
}
