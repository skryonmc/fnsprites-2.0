package com.fnspirits.client;

import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModItems;
import com.fnspirits.spirit.AlbumTracker;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SpiritAlbumScreen extends Screen {
    private static final int COLS = 3, CELL_W = 110, CELL_H = 34;
    private final long mask;

    public SpiritAlbumScreen(long mask) {
        super(Component.translatable("screen.fnspirits.album"));
        this.mask = mask;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        SpiritType[] types = SpiritType.values();
        int rows = (types.length + COLS - 1) / COLS;
        int left = (width - COLS * CELL_W) / 2;
        int top = (height - rows * CELL_H) / 2 + 12;

        int found = Long.bitCount(mask);
        int total = types.length * SpiritVariant.values().length;
        g.drawCenteredString(font, title, width / 2, top - 30, 0xFFFFFF);
        g.drawCenteredString(font, Component.translatable("screen.fnspirits.progress", found, total), width / 2, top - 18, 0xAAAAAA);

        Component hover = null;
        for (int i = 0; i < types.length; i++) {
            SpiritType t = types[i];
            int cx = left + (i % COLS) * CELL_W;
            int cy = top + (i / COLS) * CELL_H;
            g.fill(cx + 2, cy + 2, cx + CELL_W - 2, cy + CELL_H - 2, 0x66000000);
            g.drawString(font, Component.translatable("spirit.fnspirits." + t.id), cx + 8, cy + 6, 0xFFFFFF);

            for (SpiritVariant v : SpiritVariant.values()) {
                int ix = cx + 8 + v.ordinal() * 24;
                int iy = cy + 16;
                boolean seen = (mask & (1L << AlbumTracker.bit(t, v))) != 0;
                ItemStack stack = new ItemStack(ModItems.get(t, v));
                if (!seen) RenderSystem.setShaderColor(0.05f, 0.05f, 0.08f, 1f); // silueta
                g.renderItem(stack, ix, iy);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                if (mx >= ix && mx < ix + 16 && my >= iy && my < iy + 16) {
                    hover = seen ? stack.getHoverName() : Component.translatable("tooltip.fnspirits.unknown");
                }
            }
        }
        super.render(g, mx, my, pt);
        if (hover != null) g.renderTooltip(font, hover, mx, my);
    }

    @Override public boolean isPauseScreen() { return false; }
}
