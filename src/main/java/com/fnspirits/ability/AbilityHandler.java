package com.fnspirits.ability;

import com.fnspirits.SpiritsMod;
import com.fnspirits.item.SpiritItem;
import com.fnspirits.spirit.SpiritData;
import com.fnspirits.spirit.SpiritRewards;
import com.fnspirits.spirit.SpiritType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/** Habilidades. El espiritu se "equipa" al tenerlo en la segunda mano. */
@Mod.EventBusSubscriber(modid = SpiritsMod.MODID)
public class AbilityHandler {
    private static final UUID SONIC_ID = UUID.fromString("5b0f6a10-0001-4c2a-9a10-aa00000000a1");
    private static final UUID SHADOW_ID = UUID.fromString("5b0f6a10-0002-4c2a-9a10-aa00000000a2");

    static ItemStack eq(Player p, SpiritType t) {
        ItemStack s = p.getOffhandItem();
        return (s.getItem() instanceof SpiritItem i && i.type() == t) ? s : ItemStack.EMPTY;
    }

    static ItemStack anyEq(Player p) {
        ItemStack s = p.getOffhandItem();
        return s.getItem() instanceof SpiritItem ? s : ItemStack.EMPTY;
    }

    static void mod(Player p, Attribute a, UUID id, double amt) {
        AttributeInstance in = p.getAttribute(a);
        if (in == null) return;
        AttributeModifier cur = in.getModifier(id);
        if (amt <= 0) { if (cur != null) in.removeModifier(id); return; }
        if (cur != null && cur.getAmount() == amt) return;
        if (cur != null) in.removeModifier(id);
        in.addTransientModifier(new AttributeModifier(id, "fnspirits", amt, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    // ---------------- TICK ----------------
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;
        long now = p.level().getGameTime();
        CompoundTag pd = p.getPersistentData();

        // SONIC: velocidad segun nivel
        ItemStack sonic = eq(p, SpiritType.SONIC);
        mod(p, Attributes.MOVEMENT_SPEED, SONIC_ID, sonic.isEmpty() ? 0 : 0.06 * SpiritData.getLevel(sonic));

        // SHADOW: recarga de ataque mas rapida (espadas, hachas, etc.)
        ItemStack shadow = eq(p, SpiritType.SHADOW);
        mod(p, Attributes.ATTACK_SPEED, SHADOW_ID, shadow.isEmpty() ? 0 : 0.12 * SpiritData.getLevel(shadow));

        tickTails(p, now, pd);
        tickArbustin(p, pd);

        // HACKER: mejores ofertas, glow a mobs a menos de 50 bloques
        ItemStack hacker = eq(p, SpiritType.HACKER);
        if (!hacker.isEmpty() && now % 40 == 0) {
            p.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 100, 0, true, false));
            for (Monster m : p.level().getEntitiesOfClass(Monster.class, p.getBoundingBox().inflate(50))) {
                m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
            }
            // TODO Parte 2: suerte al minar (loot modifier) y rareza de espiritus en cofres
        }
    }

    private static void tickTails(ServerPlayer p, long now, CompoundTag pd) {
        ItemStack tails = eq(p, SpiritType.TAILS);
        boolean active = pd.getBoolean("fnspirits_tails");
        long end = pd.getLong("fnspirits_tails_end");

        if (active && (end <= now || tails.isEmpty())) {
            if (!p.isCreative() && !p.isSpectator()) {
                p.getAbilities().mayfly = false;
                p.getAbilities().flying = false;
                p.onUpdateAbilities();
            }
            pd.putBoolean("fnspirits_tails", false);
            pd.putLong("fnspirits_tails_safe", now + 100); // 5s sin dano de caida
            return;
        }
        // Activar: agacharse en el aire mientras caes
        if (!active && !tails.isEmpty() && !p.onGround() && p.isShiftKeyDown()
                && p.getDeltaMovement().y < 0 && SpiritData.ready(tails, p.level())) {
            int lvl = SpiritData.getLevel(tails);
            p.getAbilities().mayfly = true;
            p.getAbilities().flying = true;
            p.onUpdateAbilities();
            pd.putBoolean("fnspirits_tails", true);
            pd.putLong("fnspirits_tails_end", now + 60 + 30L * lvl);
            SpiritData.startCooldown(tails, p.level());
        }
    }

    private static void tickArbustin(ServerPlayer p, CompoundTag pd) {
        ItemStack s = eq(p, SpiritType.ARBUSTIN);
        if (s.isEmpty() || !p.isShiftKeyDown() || !p.onGround()) { pd.putInt("fnspirits_sneak", 0); return; }
        int t = pd.getInt("fnspirits_sneak") + 1;
        pd.putInt("fnspirits_sneak", t);
        if (t >= 40 && SpiritData.ready(s, p.level())) { // mantener agachado 2s
            int lvl = SpiritData.getLevel(s);
            p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100 + 60 * lvl, 0, false, false));
            SpiritData.startCooldown(s, p.level());
            pd.putInt("fnspirits_sneak", 0);
        }
    }

    // ---------------- DANO ----------------
    @SubscribeEvent
    public static void onFall(LivingFallEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        CompoundTag pd = p.getPersistentData();
        long now = p.level().getGameTime();
        if (pd.getBoolean("fnspirits_tails") || pd.getLong("fnspirits_tails_safe") > now) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent e) {
        LivingEntity victim = e.getEntity();

        // JONESY: al recibir dano, regeneras
        if (victim instanceof ServerPlayer p) {
            ItemStack j = eq(p, SpiritType.JONESY);
            if (!j.isEmpty() && SpiritData.ready(j, p.level())) {
                int lvl = SpiritData.getLevel(j);
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60 + 20 * lvl, lvl >= 4 ? 1 : 0));
                SpiritData.startCooldown(j, p.level());
            }
        }

        if (!(e.getSource().getEntity() instanceof ServerPlayer atk)) return;

        // KILL SWITCH: mas dano con flechas
        if (e.getSource().getDirectEntity() instanceof AbstractArrow) {
            ItemStack k = eq(atk, SpiritType.KILL_SWITCH);
            if (!k.isEmpty()) e.setAmount((float) (e.getAmount() * (1 + 0.12 * SpiritData.getLevel(k))));
        }

        // 8 BITS: el golpeado flota unos segundos
        ItemStack b = eq(atk, SpiritType.BITS8);
        if (!b.isEmpty() && SpiritData.ready(b, atk.level())) {
            victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30 + 10 * SpiritData.getLevel(b), 0));
            SpiritData.startCooldown(b, atk.level());
        }

        // TORMENTA: con tormenta, tus golpes invocan un rayo (mas preciso con el nivel)
        ItemStack st = eq(atk, SpiritType.STORM);
        if (!st.isEmpty() && atk.level() instanceof ServerLevel sl && sl.isThundering() && SpiritData.ready(st, sl)) {
            int lvl = SpiritData.getLevel(st);
            if (sl.random.nextDouble() < 0.25 + 0.15 * lvl) {
                double off = (SpiritData.MAX_LEVEL - lvl) * 0.8;
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
                if (bolt != null) {
                    bolt.moveTo(victim.getX() + (sl.random.nextDouble() - 0.5) * 2 * off, victim.getY(),
                            victim.getZ() + (sl.random.nextDouble() - 0.5) * 2 * off);
                    sl.addFreshEntity(bolt);
                }
                SpiritData.startCooldown(st, sl);
            }
        }
    }

    // ---------------- USO DE ITEMS ----------------
    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        ItemStack used = e.getItem();

        // SHADOW: arcos, ballestas y tridentes cargan mas rapido
        ItemStack sh = eq(p, SpiritType.SHADOW);
        if (!sh.isEmpty() && (used.getItem() instanceof BowItem || used.getItem() instanceof CrossbowItem || used.getItem() instanceof TridentItem)) {
            int lvl = SpiritData.getLevel(sh);
            if (lvl >= 2) e.setDuration(e.getDuration() - (lvl >= 4 ? 2 : 1));
        }
        // KLOMBO: comes mas rapido
        ItemStack kl = eq(p, SpiritType.KLOMBO);
        if (!kl.isEmpty() && used.getUseAnimation() == UseAnim.EAT) {
            e.setDuration(e.getDuration() - SpiritData.getLevel(kl));
        }
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        ItemStack kl = eq(p, SpiritType.KLOMBO);
        if (!kl.isEmpty() && e.getItem().isEdible()) SpiritData.addXp(p, kl, 1); // Klombo sube comiendo
    }

    // ---------------- KILLS / XP ----------------
    @SubscribeEvent
    public static void onKill(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        if (e.getEntity() instanceof Zombie) SpiritRewards.onZombieKill(p);
        ItemStack s = anyEq(p);
        if (s.isEmpty() || !(e.getEntity() instanceof Monster)) return;
        if (((SpiritItem) s.getItem()).type() == SpiritType.KLOMBO) return; // Klombo solo sube comiendo
        SpiritData.addXp(p, s, 1);
    }
}
