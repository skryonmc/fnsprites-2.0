package com.fnspirits.client;

import com.fnspirits.SpiritsMod;
import com.fnspirits.entity.SpiritEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpiritModel extends GeoModel<SpiritEntity> {
    private static final ResourceLocation ANIM = new ResourceLocation(SpiritsMod.MODID, "animations/entity/spirit.animation.json");

    @Override
    public ResourceLocation getModelResource(SpiritEntity e) {
        return new ResourceLocation(SpiritsMod.MODID, "geo/entity/spirit_" + e.getSpiritType().id + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpiritEntity e) {
        return new ResourceLocation(SpiritsMod.MODID, "textures/entity/spirit_" + e.getSpiritType().id + "_" + e.getVariant().id + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpiritEntity e) { return ANIM; }
}
