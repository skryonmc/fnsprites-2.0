package com.fnspirits.loot;

import com.fnspirits.item.SpiritItem;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritType;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

/** Hacker: probabilidad de duplicar el botin de las menas (suerte al minar). */
public class HackerMiningModifier extends LootModifier {
    public static final Supplier<Codec<HackerMiningModifier>> CODEC =
            Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, HackerMiningModifier::new)));

    public HackerMiningModifier(LootItemCondition[] conditions) { super(conditions); }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext ctx) {
        Entity e = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
        BlockState st = ctx.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (!(e instanceof Player p) || st == null || !st.is(Tags.Blocks.ORES)) return loot;
        ItemStack s = p.getOffhandItem();
        if (!(s.getItem() instanceof SpiritItem si) || si.type() != SpiritType.HACKER) return loot;
        ItemStack tool = ctx.getParamOrNull(LootContextParams.TOOL);
        if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) return loot;

        if (ctx.getRandom().nextDouble() < 0.10 * SpiritData.getLevel(s)) {
            ObjectArrayList<ItemStack> extra = new ObjectArrayList<>();
            for (ItemStack d : loot) extra.add(d.copy());
            loot.addAll(extra);
        }
        return loot;
    }

    @Override
    public Codec<? extends net.minecraftforge.common.loot.IGlobalLootModifier> codec() { return CODEC.get(); }
}
