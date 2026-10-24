package com.fnspirits.spirit;

public enum SpiritVariant {
    NORMAL("normal", 1.0),
    GREEN("green", 1.5),   // efecto: numeros moviendose (Parte 2)
    GOLD("gold", 2.5);     // efecto: brillo que se mueve (Parte 2), mas dificil de subir

    public final String id;
    public final double xpMultiplier;

    SpiritVariant(String id, double xpMultiplier) {
        this.id = id;
        this.xpMultiplier = xpMultiplier;
    }
}
