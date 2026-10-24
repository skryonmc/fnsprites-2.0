package com.fnspirits.network;

import com.fnspirits.SpiritsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SpiritsMod.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.registerMessage(0, DoubleJumpPacket.class, DoubleJumpPacket::encode, DoubleJumpPacket::decode, DoubleJumpPacket::handle);
        CHANNEL.registerMessage(1, OpenAlbumPacket.class, OpenAlbumPacket::encode, OpenAlbumPacket::decode, OpenAlbumPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private ModNetwork() {}
}
