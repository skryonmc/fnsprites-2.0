package com.fnspirits.entity;

import com.fnspirits.registry.ModEntities;
import com.fnspirits.registry.ModItems;
import com.fnspirits.registry.ModParticles;
import com.fnspirits.spirit.SpiritRarity;
import com.fnspirits.spirit.SpiritType;
import com.fnspirits.spirit.SpiritVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Espiritu errante: aparece raro, vaga despacio y se recoge con clic derecho. */
public class SpiritEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(SpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SpiritEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.spirit.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.spirit.walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private CompoundTag itemTag = new CompoundTag(); // nivel/xp si viene de un item soltado
    private boolean wild = false;                    // espiritus naturales desaparecen con el tiempo
    private int wildTicks = 0;

    public SpiritEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.18);
    }

    public static boolean checkSpawn(EntityType<SpiritEntity> t, LevelAccessor l, MobSpawnType r, BlockPos p, RandomSource rand) {
        return Mob.checkMobSpawnRules(t, l, r, p, rand) && rand.nextInt(3) == 0;
    }

    // ------------- datos -------------
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_TYPE, 0);
        entityData.define(DATA_VARIANT, 0);
    }

    public SpiritType getSpiritType() { return SpiritType.values()[entityData.get(DATA_TYPE) % SpiritType.values().length]; }
    public SpiritVariant getVariant() { return SpiritVariant.values()[entityData.get(DATA_VARIANT) % SpiritVariant.values().length]; }

    public void setSpirit(SpiritType t, SpiritVariant v) {
        entityData.set(DATA_TYPE, t.ordinal());
        entityData.set(DATA_VARIANT, v.ordinal());
    }

    public void setItemTag(CompoundTag tag) { this.itemTag = tag == null ? new CompoundTag() : tag.copy(); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("SpiritType", getSpiritType().id);
        tag.putString("SpiritVariant", getVariant().id);
        tag.put("ItemTag", itemTag);
        tag.putBoolean("Wild", wild);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        SpiritType t = getSpiritType();
        SpiritVariant v = getVariant();
        for (SpiritType x : SpiritType.values()) if (x.id.equals(tag.getString("SpiritType"))) t = x;
        for (SpiritVariant x : SpiritVariant.values()) if (x.id.equals(tag.getString("SpiritVariant"))) v = x;
        setSpirit(t, v);
        itemTag = tag.getCompound("ItemTag");
        wild = tag.getBoolean("Wild");
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor l, DifficultyInstance d, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        setSpirit(SpiritRarity.rollType(random, 0), SpiritRarity.rollVariant(random, 0));
        wild = reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION;
        return super.finalizeSpawn(l, d, reason, data, tag);
    }

    // ------------- comportamiento -------------
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6, 120)); // vaga poco
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6f));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        // Los mobs no lo matan; solo el vacio o el creativo
        return src.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(src, amount);
    }

    @Override
    public boolean removeWhenFarAway(double dist) { return wild; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            clientEffects();
        } else if (wild && ++wildTicks > 6000) { // 5 minutos
            discard();
        }
    }

    private void clientEffects() {
        SpiritVariant v = getVariant();
        if (v == SpiritVariant.GREEN && tickCount % 3 == 0) {
            level().addParticle(ModParticles.DIGIT.get(), getX() + (random.nextDouble() - 0.5) * 0.6,
                    getY() + 0.5 + random.nextDouble() * 0.6, getZ() + (random.nextDouble() - 0.5) * 0.6, 0, 0, 0);
        } else if (v == SpiritVariant.GOLD && tickCount % 2 == 0) {
            double a = tickCount * 0.25;
            level().addParticle(ModParticles.SHINE.get(), getX() + Math.cos(a) * 0.45, getY() + 0.5 + Math.sin(tickCount * 0.1) * 0.3,
                    getZ() + Math.sin(a) * 0.45, -Math.sin(a) * 0.04, 0.01, Math.cos(a) * 0.04);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!level().isClientSide) {
            ItemStack stack = new ItemStack(ModItems.get(getSpiritType(), getVariant()));
            if (!itemTag.isEmpty()) stack.setTag(itemTag.copy());
            if (!player.getInventory().add(stack)) player.drop(stack, false);
            level().playSound(null, blockPosition(), SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 1f, getVoicePitch());
            discard();
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    // ------------- sonidos (provisionales: sonidos del Allay con tono distinto por espiritu) -------------
    @Nullable @Override protected SoundEvent getAmbientSound() { return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM; }
    @Override public int getAmbientSoundInterval() { return 240; }
    @Override public float getVoicePitch() { return 0.9f + getSpiritType().ordinal() * 0.07f; }

    // ------------- animaciones GeckoLib -------------
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state ->
                state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
