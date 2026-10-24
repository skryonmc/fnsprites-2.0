package com.fnspirits.spirit;

public enum SpiritType {
    TAILS("tails", 1200),
    SONIC("sonic", 0),
    SHADOW("shadow", 0),
    STORM("storm", 200),
    JONESY("jonesy", 600),
    CORONA("corona", 0),
    HACKER("hacker", 0),
    KLOMBO("klombo", 0),
    JACK_RABBIT("jack_rabbit", 100),
    ARBUSTIN("arbustin", 1800),
    AVENTURERO("aventurero", 0),
    BITS8("bits8", 300),
    KILL_SWITCH("kill_switch", 0);

    public final String id;
    /** Cooldown base en ticks (nivel 1). 0 = habilidad pasiva. */
    public final int baseCooldown;

    SpiritType(String id, int baseCooldown) {
        this.id = id;
        this.baseCooldown = baseCooldown;
    }
}
