package com.fnspirits.network;

import com.fnspirits.ability.JackRabbitHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpPacket {
    public static void encode(DoubleJumpPacket p, FriendlyByteBuf buf) {}

    public static DoubleJumpPacket decode(FriendlyByteBuf buf) { return new DoubleJumpPacket(); }

    public static void handle(DoubleJumpPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender != null) JackRabbitHandler.tryDoubleJump(sender);
        });
        ctx.get().setPacketHandled(true);
    }
}
