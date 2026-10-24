package com.fnspirits.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class DigitParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected DigitParticle(ClientLevel l, double x, double y, double z, SpriteSet sprites) {
        super(l, x, y, z);
        this.sprites = sprites;
        this.pickSprite(sprites);
        this.setColor(0.25f, 1f, 0.35f);
        this.lifetime = 20 + this.random.nextInt(20);
        this.quadSize = 0.07f + this.random.nextFloat() * 0.03f;
        this.gravity = 0;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = -0.03;
        this.zd = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (this.age % 3 == 0) this.pickSprite(sprites); // el numero cambia mientras cae
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
            return new DigitParticle(l, x, y, z, sprites);
        }
    }
}
