package com.fnspirits.item;

import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiritItem extends Item {
    private final SpiritType type;
    private final SpiritVariant variant;

    public SpiritItem(SpiritType type, SpiritVariant variant) {
        super(new Item.Properties().stacksTo(1));
        this.type = type;
        this.variant = variant;
    }

    public SpiritType type() { return type; }
    public SpiritVariant variant() { return variant; }

    @Override
    public boolean isFoil(ItemStack stack) {
        return variant == SpiritVariant.GOLD;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, net.minecraft.world.item.TooltipFlag flag) {
        int lvl = SpiritData.getLevel(stack);
        tip.add(Component.translatable("tooltip.fnspirits.level", lvl, SpiritData.MAX_LEVEL).withStyle(ChatFormatting.AQUA));
        if (lvl < SpiritData.MAX_LEVEL) {
            tip.add(Component.literal("XP: " + SpiritData.getXp(stack) + "/" + SpiritData.xpNeeded(stack)).withStyle(ChatFormatting.GRAY));
        }
        tip.add(Component.translatable("ability.fnspirits." + type.id).withStyle(ChatFormatting.YELLOW));
        if (type.baseCooldown > 0) {
            tip.add(Component.translatable("tooltip.fnspirits.cooldown", SpiritData.cooldownTicks(type, lvl) / 20).withStyle(ChatFormatting.DARK_GRAY));
        }
        tip.add(Component.translatable("tooltip.fnspirits.equip").withStyle(ChatFormatting.DARK_GRAY));
    }
}
