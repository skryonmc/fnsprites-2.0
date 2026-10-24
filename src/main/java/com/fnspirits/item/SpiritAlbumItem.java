package com.fnspirits.item;

import com.fnspirits.network.ModNetwork;
import com.fnspirits.network.OpenAlbumPacket;
import com.fnspirits.spirit.AlbumTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

/** Album: clic derecho para ver todos los espiritus que has descubierto. */
public class SpiritAlbumItem extends Item {
    public SpiritAlbumItem() { super(new Item.Properties().stacksTo(1)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p, InteractionHand hand) {
        if (!level.isClientSide && p instanceof ServerPlayer sp) {
            AlbumTracker.scan(sp);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new OpenAlbumPacket(AlbumTracker.getMask(sp)));
        }
        return InteractionResultHolder.sidedSuccess(p.getItemInHand(hand), level.isClientSide);
    }
}
