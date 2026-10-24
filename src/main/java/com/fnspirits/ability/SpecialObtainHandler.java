package com.fnspirits.ability;

import com.fnspirits.SpiritsMod;
import com.fnspirits.entity.SpiritPop;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Espiritus que se consiguen de forma especifica (una vez por jugador). Aparecen como entidad cerca de ti.
 *  - Klombo: comer 500 alimentos (muy dificil).
 *  - Aventurero: minar 150 menas.
 *  - Tormenta: que te caiga un rayo.
 *  - Kill Switch: matar un esqueleto con una flecha desde 30+ bloques.
 */
@Mod.EventBusSubscriber(modid = SpiritsMod.MODID)
public class SpecialObtainHandler {
    static final int FOOD_GOAL = 500;
    static final int ORE_GOAL = 150;
    static final double SNIPE_DISTANCE = 30;

    private static void grant(ServerPlayer p, String id, SpiritType t) {
        CompoundTag pd = p.getPersistentData();
        String flag = "fnspirits_got_" + id;
        if (pd.getBoolean(flag)) return;
        pd.putBoolean(flag, true);
        Vec3 pos = p.position().add(p.getLookAngle().multiply(1.5, 0, 1.5)).add(0, 0.6, 0);
        SpiritPop.spawn((ServerLevel) p.level(), pos, t, SpiritVariant.NORMAL, true);
        p.sendSystemMessage(Component.translatable("msg.fnspirits.special." + id));
    }

    private static int count(ServerPlayer p, String key) {
        CompoundTag pd = p.getPersistentData();
        int n = pd.getInt(key) + 1;
        pd.putInt(key, n);
        return n;
    }

    @SubscribeEvent
    public static void onEat(LivingEntityUseItemEvent.Finish e) {
        if (e.getEntity() instanceof ServerPlayer p && e.getItem().isEdible()) {
            if (count(p, "fnspirits_foods") >= FOOD_GOAL) grant(p, "klombo", SpiritType.KLOMBO);
        }
    }

    @SubscribeEvent
    public static void onMine(BlockEvent.BreakEvent e) {
        if (e.getPlayer() instanceof ServerPlayer p && e.getState().is(Tags.Blocks.ORES)) {
            if (count(p, "fnspirits_ores") >= ORE_GOAL) grant(p, "aventurero", SpiritType.AVENTURERO);
        }
    }

    @SubscribeEvent
    public static void onLightning(EntityStruckByLightningEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) grant(p, "storm", SpiritType.STORM);
    }

    @SubscribeEvent
    public static void onSnipe(LivingDeathEvent e) {
        if (e.getEntity() instanceof AbstractSkeleton sk && e.getSource().getDirectEntity() instanceof AbstractArrow
                && e.getSource().getEntity() instanceof ServerPlayer p && p.distanceTo(sk) >= SNIPE_DISTANCE) {
            grant(p, "killswitch", SpiritType.KILL_SWITCH);
        }
    }
}
