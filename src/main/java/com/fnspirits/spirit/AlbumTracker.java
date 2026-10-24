package com.fnspirits.spirit;

import com.fnspirits.SpiritsMod;
import com.fnspirits.item.SpiritItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Registra que espiritus has tenido en el inventario (para el Album). 1 bit por tipo+variante. */
@Mod.EventBusSubscriber(modid = SpiritsMod.MODID)
public class AlbumTracker {
    private static final String KEY = "fnspirits_seen";

    public static int bit(SpiritType t, SpiritVariant v) {
        return t.ordinal() * SpiritVariant.values().length + v.ordinal();
    }

    public static long getMask(Player p) { return p.getPersistentData().getLong(KEY); }

    public static void scan(ServerPlayer p) {
        long mask = getMask(p);
        long old = mask;
        for (ItemStack s : p.getInventory().items) mask = mark(mask, s);
        for (ItemStack s : p.getInventory().offhand) mask = mark(mask, s);
        if (mask != old) p.getPersistentData().putLong(KEY, mask);
    }

    private static long mark(long mask, ItemStack s) {
        return s.getItem() instanceof SpiritItem si ? mask | (1L << bit(si.type(), si.variant())) : mask;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player instanceof ServerPlayer p && p.tickCount % 20 == 0) scan(p);
    }
}
