package com.fnspirits.client;

import com.fnspirits.block.PedestalBlockEntity;
import com.fnspirits.entity.SpiritEntity;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.item.ItemStack;

/** Dibuja el espiritu del pedestal con su modelo GeckoLib, girando lentamente y flotando. */
public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    public PedestalRenderer(BlockEntityRendererProvider.Context ctx) {}

    @SuppressWarnings("unchecked")
    @Override
    public void render(PedestalBlockEntity be, float pt, PoseStack pose, MultiBufferSource buf, int light, int overlay) {
        ItemStack s = be.getItem();
        if (be.getLevel() == null || !(s.getItem() instanceof SpiritItem si)) { be.renderDummy = null; return; }

        SpiritEntity d = be.renderDummy;
        if (d == null) {
            d = new SpiritEntity(ModEntities.SPIRIT.get(), be.getLevel());
            be.renderDummy = d;
        }
        d.setSpirit(si.type(), si.variant());
        float time = be.getLevel().getGameTime() + pt;
        d.tickCount = (int) time;
        float angle = (time * 3f) % 360f;
        d.yBodyRot = angle; d.yBodyRotO = angle;
        d.yHeadRot = angle; d.yHeadRotO = angle;

        pose.pushPose();
        pose.translate(0.5, 0.78 + Math.sin(time * 0.1) * 0.04, 0.5);
        pose.scale(0.9f, 0.9f, 0.9f);
        int l = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());
        EntityRenderer<? super SpiritEntity> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(d);
        ((EntityRenderer<SpiritEntity>) r).render(d, 0f, pt, pose, buf, l);
        pose.popPose();
    }
}
