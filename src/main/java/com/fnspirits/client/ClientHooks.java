package com.fnspirits.client;

import net.minecraft.client.Minecraft;

public final class ClientHooks {
    private ClientHooks() {}

    public static void openAlbum(long mask) {
        Minecraft.getInstance().setScreen(new SpiritAlbumScreen(mask));
    }
}
