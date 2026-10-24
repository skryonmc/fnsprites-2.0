package com.fnspirits.spirit;

import com.fnspirits.item.SpiritItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class SpiritData {
    public static final int MAX_LEVEL = 5;
    // Multiplicador de cooldown por nivel: nivel 5 casi sin cooldown
    private static final double[] CD_FACTOR = {1.0, 0.7, 0.45, 0.25, 0.05};

    private SpiritData() {}

    public static int getLevel(ItemStack s) {
        return Mth.clamp(s.getOrCreateTag().getInt("Level"), 1, MAX_LEVEL);
    }

    public static int getXp(ItemStack s) {
        return s.getOrCreateTag().getInt("Xp");
    }

    public static int xpNeeded(ItemStack s) {
        if (!(s.getItem() instanceof SpiritItem it)) return Integer.MAX_VALUE;
        return (int) (20 * Math.pow(2, getLevel(s) - 1) * it.variant().xpMultiplier);
    }

    public static void addXp(ServerPlayer p, ItemStack s, int amount) {
        if (!(s.getItem() instanceof SpiritItem it) || getLevel(s) >= MAX_LEVEL) return;
        CompoundTag tag = s.getOrCreateTag();
        int xp = tag.getInt("Xp") + amount;
        while (getLevel(s) < MAX_LEVEL && xp >= xpNeeded(s)) {
            xp -= xpNeeded(s);
            tag.putInt("Level", getLevel(s) + 1);
            onLevelUp(p, s, it);
        }
        tag.putInt("Xp", getLevel(s) >= MAX_LEVEL ? 0 : xp);
    }

    private static void onLevelUp(ServerPlayer p, ItemStack s, SpiritItem it) {
        int lvl = getLevel(s);
        p.displayClientMessage(Component.translatable("msg.fnspirits.levelup", s.getHoverName(), lvl), true);
        p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1.2f);
        // Aventurero: un mineral por cada subida de nivel
        if (it.type() == SpiritType.AVENTURERO) {
            ItemStack reward = switch (lvl) {
                case 2 -> new ItemStack(Items.IRON_INGOT, 8);
                case 3 -> new ItemStack(Items.GOLD_INGOT, 8);
                case 4 -> new ItemStack(Items.DIAMOND, 3);
                default -> new ItemStack(Items.EMERALD, 8);
            };
            if (!p.getInventory().add(reward)) p.drop(reward, false);
        }
    }

    public static int cooldownTicks(SpiritType t, int level) {
        return (int) (t.baseCooldown * CD_FACTOR[Mth.clamp(level, 1, MAX_LEVEL) - 1]);
    }

    public static boolean ready(ItemStack s, Level w) {
        return w.getGameTime() >= s.getOrCreateTag().getLong("CdEnd");
    }

    public static void startCooldown(ItemStack s, Level w) {
        if (!(s.getItem() instanceof SpiritItem it)) return;
        s.getOrCreateTag().putLong("CdEnd", w.getGameTime() + cooldownTicks(it.type(), getLevel(s)));
    }
}
