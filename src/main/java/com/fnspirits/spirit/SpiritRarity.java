package com.fnspirits.spirit;

import net.minecraft.util.RandomSource;

import java.util.List;

/** Rareza de espiritus encontrados en cofres. Corona, Hacker y Klombo NO salen de cofres normales. */
public final class SpiritRarity {
    private static final List<SpiritType> COMMON = List.of(SpiritType.ARBUSTIN, SpiritType.AVENTURERO, SpiritType.JONESY, SpiritType.JACK_RABBIT);
    private static final List<SpiritType> UNCOMMON = List.of(SpiritType.SONIC, SpiritType.TAILS, SpiritType.KILL_SWITCH, SpiritType.BITS8);
    private static final List<SpiritType> RARE = List.of(SpiritType.SHADOW, SpiritType.STORM);

    private SpiritRarity() {}

    /** hackerLevel 0 = sin Hacker equipado. Sube la probabilidad de tipos raros. */
    public static SpiritType rollType(RandomSource r, int hackerLevel) {
        double c = 60, u = 30 * (1 + 0.2 * hackerLevel), ra = 10 * (1 + 0.6 * hackerLevel);
        double x = r.nextDouble() * (c + u + ra);
        List<SpiritType> list = x < c ? COMMON : x < c + u ? UNCOMMON : RARE;
        return list.get(r.nextInt(list.size()));
    }

    public static SpiritVariant rollVariant(RandomSource r, int hackerLevel) {
        double n = 80, g = 17 * (1 + 0.3 * hackerLevel), gold = 3 * (1 + 0.8 * hackerLevel);
        double x = r.nextDouble() * (n + g + gold);
        return x < n ? SpiritVariant.NORMAL : x < n + g ? SpiritVariant.GREEN : SpiritVariant.GOLD;
    }
}
