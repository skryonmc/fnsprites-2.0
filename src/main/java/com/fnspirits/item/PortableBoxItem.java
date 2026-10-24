package com.fnspirits.item;

import com.fnspirits.entity.SpiritSpawner;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Caja portatil: mantén clic derecho 2s y extrae UN espiritu al azar delante de ti. Se consume. */
public class PortableBoxItem extends Item {
    public PortableBoxItem() { super(new Item.Properties().stacksTo(16)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p, InteractionHand hand) {
        ItemStack s = p.getItemInHand(hand);
        p.startUsingItem(hand);
        return InteractionResultHolder.consume(s);
    }

    @Override public int getUseDuration(ItemStack s) { return 40; }
    @Override public UseAnim getUseAnimation(ItemStack s) { return UseAnim.BOW; }

    @Override
    public void onUseTick(Level level, LivingEntity e, ItemStack s, int remaining) {
        if (level instanceof ServerLevel sl && remaining % 4 == 0) {
            sl.sendParticles(ParticleTypes.ENCHANT, e.getX(), e.getY() + 1, e.getZ(), 8, 0.6, 0.6, 0.6, 0.1);
            sl.playSound(null, e.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6f, 0.8f + (40 - remaining) * 0.02f);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack s, Level level, LivingEntity e) {
        if (level instanceof ServerLevel sl && e instanceof ServerPlayer p) {
            SpiritSpawner.release(sl, p.position().add(p.getLookAngle().scale(1.5)).add(0, 0.5, 0), p);
            if (!p.getAbilities().instabuild) s.shrink(1);
            p.getCooldowns().addCooldown(this, 20);
        }
        return s;
    }
}
