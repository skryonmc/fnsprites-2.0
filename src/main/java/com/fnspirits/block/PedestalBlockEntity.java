package com.fnspirits.block;

import com.fnspirits.entity.SpiritEntity;
import com.fnspirits.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends BlockEntity {
    private ItemStack item = ItemStack.EMPTY;
    /** Solo cliente: entidad "fantasma" para dibujar el modelo (ver PedestalRenderer). */
    public SpiritEntity renderDummy;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PEDESTAL_BE.get(), pos, state);
    }

    public ItemStack getItem() { return item; }

    public void setItem(ItemStack s) {
        this.item = s;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Item", item.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        item = ItemStack.of(tag.getCompound("Item"));
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Nullable @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
