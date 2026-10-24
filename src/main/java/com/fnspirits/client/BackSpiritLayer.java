package com.fnspirits.client;

import com.fnspirits.entity.SpiritEntity;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.registry.ModEntities;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dibuja el espiritu de la segunda mano en la espalda del jugador.
 * Usa una entidad "fantasma" (nunca se agrega al mundo) renderizada con GeckoLib,
 * asi reutiliza modelo, textura y animaciones de la Parte 3.
 */
public class BackSpiritLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final Map<UUID, Entry> CACHE = new HashMap<>();

    private static final class Entry {
        final SpiritEntity entity;
        final SpiritType type;
        final SpiritVariant variant;
        final long start;
        Entry(SpiritEntity e, SpiritType t, SpiritVariant v, long start) { entity = e; type = t; variant = v; this.start = start; }
    }

    public BackSpiritLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    public static void clearCache() { CACHE.clear(); }

    @SuppressWarnings("unchecked")
    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (player.isSpectator() || player.isInvisible()) return;
        ItemStack stack = player.getOffhandItem();
        if (!(stack.getItem() instanceof SpiritItem item)) {
            CACHE.remove(player.getUUID());
            return;
        }

        long now = player.level().getGameTime();
        Entry en = CACHE.get(player.getUUID());
        if (en == null || en.type != item.type() || en.variant != item.variant()) {
            SpiritEntity dummy = new SpiritEntity(ModEntities.SPIRIT.get(), player.level());
            dummy.setSpirit(item.type(), item.variant());
            en = new Entry(dummy, item.type(), item.variant(), now);
            CACHE.put(player.getUUID(), en);
        }
        SpiritEntity dummy = en.entity;
        dummy.tickCount = player.tickCount;
        dummy.yBodyRot = 0; dummy.yBodyRotO = 0;     // mira hacia atras
        dummy.yHeadRot = 0; dummy.yHeadRotO = 0;
        dummy.walkAnimation.update(Math.min(1f, limbSwingAmount * 2f), 1f); // camina cuando tu caminas

        // animacion de aparicion al equiparlo: "pop" de 8 ticks
        float t = Mth.clamp((now + partialTick - en.start) / 8f, 0f, 1f);
        float pop = 1f + 2.70158f * (float) Math.pow(t - 1, 3) + 1.70158f * (float) Math.pow(t - 1, 2);
        pop = Math.max(pop, 0.01f);

        boolean crouch = player.isCrouching();
        boolean armor = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();

        pose.pushPose();
        pose.scale(-1f, -1f, 1f); // deshace el volteo de las capas: ejes normales, la espalda es +z
        pose.translate(0f, -0.75f - (crouch ? 0.1f : 0f), 0.27f + (armor ? 0.05f : 0f) - (crouch ? 0.15f : 0f));
        float s = 0.55f * pop;
        pose.scale(s, s, s);

        EntityRenderer<? super SpiritEntity> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dummy);
        ((EntityRenderer<SpiritEntity>) r).render(dummy, 0f, partialTick, pose, buffer, light);
        pose.popPose();
    }
}
