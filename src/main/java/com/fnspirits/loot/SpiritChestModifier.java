package com.fnspirits.loot;

import com.fnspirits.entity.SpiritPop;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

/** Al abrir un cofre generado, con poca probabilidad un espiritu SALE del cofre (con particulas y sonido). */
public class SpiritChestModifier extends LootModifier {
    public static final Supplier<Codec<SpiritChestModifier>> CODEC =
            Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SpiritChestModifier::new)));

    public static final double BASE_CHANCE = 0.06;

    public SpiritChestModifier(LootItemCondition[] conditions) { super(conditions); }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext ctx) {
        if (!ctx.getQueriedLootTableId().getPath().startsWith("chests/")) return loot;
        Vec3 origin = ctx.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) return loot;
        ServerLevel sl = ctx.getLevel();

        Entity e = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
        Player player = e instanceof Player p ? p : null;
        int hl = SpiritPop.hackerLevel(player);
        if (ctx.getRandom().nextDouble() >= BASE_CHANCE * (1 + 0.2 * hl)) return loot;

        SpiritPop.rollAndSpawn(sl, origin.add(0, 0.9, 0), player, 0, true);
        return loot;
    }

    @Override
    public Codec<? extends net.minecraftforge.common.loot.IGlobalLootModifier> codec() { return CODEC.get(); }
}
