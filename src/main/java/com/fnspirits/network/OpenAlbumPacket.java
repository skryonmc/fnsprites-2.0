package com.fnspirits.network;

import com.fnspirits.client.ClientHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenAlbumPacket {
    public final long mask;

    public OpenAlbumPacket(long mask) { this.mask = mask; }

    public static void encode(OpenAlbumPacket p, FriendlyByteBuf buf) { buf.writeLong(p.mask); }

    public static OpenAlbumPacket decode(FriendlyByteBuf buf) { return new OpenAlbumPacket(buf.readLong()); }

    public static void handle(OpenAlbumPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openAlbum(p.mask)));
        ctx.get().setPacketHandled(true);
    }
}
