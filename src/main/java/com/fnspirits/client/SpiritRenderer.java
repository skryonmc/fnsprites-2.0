package com.fnspirits.client;

import com.fnspirits.entity.SpiritEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpiritRenderer extends GeoEntityRenderer<SpiritEntity> {
    public SpiritRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpiritModel());
        this.shadowRadius = 0.3f;
    }
}
