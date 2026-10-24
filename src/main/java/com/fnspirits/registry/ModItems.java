package com.fnspirits.registry;

import com.fnspirits.SpiritsMod;
import com.fnspirits.item.PortableBoxItem;
import com.fnspirits.item.SpiritAlbumItem;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpiritsMod.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpiritsMod.MODID);

    private static final Map<SpiritType, Map<SpiritVariant, RegistryObject<Item>>> SPIRITS = new EnumMap<>(SpiritType.class);

    static {
        for (SpiritType t : SpiritType.values()) {
            Map<SpiritVariant, RegistryObject<Item>> m = new EnumMap<>(SpiritVariant.class);
            for (SpiritVariant v : SpiritVariant.values()) {
                m.put(v, ITEMS.register("spirit_" + t.id + "_" + v.id, () -> new SpiritItem(t, v)));
            }
            SPIRITS.put(t, m);
        }
    }

    public static final RegistryObject<Item> PORTABLE_BOX = ITEMS.register("portable_extraction_box", PortableBoxItem::new);
    public static final RegistryObject<Item> ALBUM = ITEMS.register("spirit_album", SpiritAlbumItem::new);
    public static final RegistryObject<Item> EXTRACTION_BOX_ITEM = ITEMS.register("extraction_box",
            () -> new BlockItem(ModBlocks.EXTRACTION_BOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> PEDESTAL_ITEM = ITEMS.register("spirit_pedestal",
            () -> new BlockItem(ModBlocks.PEDESTAL.get(), new Item.Properties()));

    public static final RegistryObject<Item> CODE_TERMINAL_ITEM = ITEMS.register("code_terminal",
            () -> new BlockItem(ModBlocks.CODE_TERMINAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> ARROW_PAD_ITEM = ITEMS.register("arrow_pad",
            () -> new BlockItem(ModBlocks.ARROW_PAD.get(), new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("spirits", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fnspirits"))
                    .icon(() -> new ItemStack(get(SpiritType.TAILS, SpiritVariant.GOLD)))
                    .displayItems((params, out) -> {
                        out.accept(ALBUM.get());
                        out.accept(PORTABLE_BOX.get());
                        out.accept(EXTRACTION_BOX_ITEM.get());
                        out.accept(PEDESTAL_ITEM.get());
                        out.accept(CODE_TERMINAL_ITEM.get());
                        out.accept(ARROW_PAD_ITEM.get());
                        SPIRITS.values().forEach(m -> m.values().forEach(o -> out.accept(o.get())));
                    })
                    .build());

    public static Item get(SpiritType t, SpiritVariant v) {
        return SPIRITS.get(t).get(v).get();
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }

    private ModItems() {}
}
