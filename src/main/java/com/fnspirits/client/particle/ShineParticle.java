package com.fnspirits.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ShineParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float baseSize;

    protected ShineParticle(ClientLevel l, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(l, x, y, z);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
        this.setColor(1f, 0.82f, 0.2f);
        this.lifetime = 14 + this.random.nextInt(8);
        this.baseSize = 0.08f + this.random.nextFloat() * 0.04f;
        this.quadSize = baseSize;
        this.gravity = 0;
        this.hasPhysics = false;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.setSpriteFromAge(sprites);
            this.quadSize = baseSize * (0.6f + 0.4f * Mth.sin(this.age * 0.9f)); // centelleo
            this.alpha = 1f - (float) this.age / this.lifetime;
        }
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    @Override
    protected int getLightColor(float partialTick) { return 15728880; }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(SimpleParticleType t, ClientLevel l, double x, double y, double z, double dx, double dy, double dz) {
            return new ShineParticle(l, x, y, z, dx, dy, dz, sprites);
        }
    }
}
