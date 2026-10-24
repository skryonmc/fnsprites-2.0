package com.fnspirits.spirit;

import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Cadena de obtencion especial. onExtract se llamara desde los santuarios/cajas (Parte 5). */
public final class SpiritRewards {
    private SpiritRewards() {}

    public static void give(ServerPlayer p, SpiritType t, SpiritVariant v) {
        ItemStack s = new ItemStack(ModItems.get(t, v));
        if (!p.getInventory().add(s)) p.drop(s, false);
        p.displayClientMessage(Component.translatable("msg.fnspirits.reward", s.getHoverName()), false);
    }

    /** 50 zombies matados -> Corona normal (rojo). */
    public static void onZombieKill(ServerPlayer p) {
        var data = p.getPersistentData();
        int kills = data.getInt("fnspirits_zombies") + 1;
        data.putInt("fnspirits_zombies", kills);
        if (kills == 50) give(p, SpiritType.CORONA, SpiritVariant.NORMAL);
    }

    /** Corona normal nv5 extraida -> Corona dorada. Corona dorada nv5 extraida -> Hacker. */
    public static void onExtract(ServerPlayer p, ItemStack s) {
        if (!(s.getItem() instanceof SpiritItem it) || it.type() != SpiritType.CORONA) return;
        if (SpiritData.getLevel(s) < SpiritData.MAX_LEVEL) return;
        if (it.variant() == SpiritVariant.NORMAL) give(p, SpiritType.CORONA, SpiritVariant.GOLD);
        else if (it.variant() == SpiritVariant.GOLD) give(p, SpiritType.HACKER, SpiritVariant.NORMAL);
    }
}
