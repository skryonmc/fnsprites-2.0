package com.fnspirits.ability;

import com.fnspirits.SpiritsMod;
import com.fnspirits.entity.SpiritPop;
import com.fnspirits.spirit.SpiritRarity;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/** Mobs hostiles matados por un jugador pueden soltar un espiritu (sale como entidad). */
@Mod.EventBusSubscriber(modid = SpiritsMod.MODID)
public class MobDropHandler {
    // Ajustes rapidos
    static final double ZOMBIE_CHANCE = 0.015;
    static final double MONSTER_CHANCE = 0.008;
    static final double STRONG_CHANCE = 0.30;

    private static final Map<EntityType<?>, SpiritType> THEMED = new HashMap<>();
    static {
        THEMED.put(EntityType.ZOMBIE, SpiritType.JONESY);
        THEMED.put(EntityType.HUSK, SpiritType.SONIC);
        THEMED.put(EntityType.SKELETON, SpiritType.KILL_SWITCH);
        THEMED.put(EntityType.STRAY, SpiritType.KILL_SWITCH);
        THEMED.put(EntityType.CREEPER, SpiritType.STORM);
        THEMED.put(EntityType.ENDERMAN, SpiritType.SHADOW);
        THEMED.put(EntityType.WITCH, SpiritType.ARBUSTIN);
        THEMED.put(EntityType.SPIDER, SpiritType.JACK_RABBIT);
        THEMED.put(EntityType.CAVE_SPIDER, SpiritType.JACK_RABBIT);
        THEMED.put(EntityType.PHANTOM, SpiritType.TAILS);
        THEMED.put(EntityType.BLAZE, SpiritType.BITS8);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p) || !(e.getEntity().level() instanceof ServerLevel sl)) return;
        LivingEntity v = e.getEntity();

        double chance;
        int bonus = 0;
        if (v instanceof EnderDragon || v instanceof WitherBoss) { chance = 1.0; bonus = 3; }
        else if (v instanceof Warden || v instanceof ElderGuardian) { chance = STRONG_CHANCE; bonus = 2; }
        else if (v instanceof Zombie) chance = ZOMBIE_CHANCE;
        else if (v instanceof Monster) chance = MONSTER_CHANCE;
        else return;

        int hl = SpiritPop.hackerLevel(p);
        if (sl.random.nextDouble() >= chance * (1 + 0.25 * hl)) return;

        SpiritType themed = THEMED.get(v.getType());
        SpiritType type = (themed != null && sl.random.nextBoolean()) ? themed : SpiritRarity.rollType(sl.random, hl + bonus);
        SpiritVariant variant = SpiritRarity.rollVariant(sl.random, hl + bonus);
        SpiritPop.spawn(sl, v.position().add(0, 0.6, 0), type, variant, bonus > 0);
    }
}
