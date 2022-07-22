package com.github.merchantpug.toomanyorigins.mixin;

import com.github.merchantpug.toomanyorigins.TooManyOrigins;
import com.github.merchantpug.toomanyorigins.registry.TMOEffects;
import com.github.merchantpug.toomanyorigins.registry.TMOPowers;
import com.github.merchantpug.toomanyorigins.util.TooManyOriginsConfig;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract EntityGroup getGroup();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void zombifyVillagerOnDeath(DamageSource source, CallbackInfo ci) {
        if (!this.isRemoved()) {
            if ((LivingEntity)(Object)this instanceof VillagerEntity) {
                if (TooManyOriginsConfig.legacyUndeadEnabled && (this.hasStatusEffect(TMOEffects.ZOMBIFYING) && source.getName().equals("zombification") || TMOPowers.LEGACY_DEATHLY_BITE.isActive(source.getAttacker()) && TMOPowers.LEGACY_DEATHLY_BITE.get(source.getAttacker()).canUse())) {
                    VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
                    ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                    if (zombieVillagerEntity != null) {
                        zombieVillagerEntity.initialize((ServerWorldAccess)villagerEntity.world, villagerEntity.world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
                        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
                        zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
                        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
                        zombieVillagerEntity.setXp(villagerEntity.getExperience());
                        villagerEntity.world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, zombieVillagerEntity.getBlockPos(), 0);
                    }
                }
            }
        }
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void makeUndeadImmuneToEffects(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        StatusEffect statusEffect = effect.getEffectType();
        if (this.getGroup() == EntityGroup.UNDEAD) {
            if (TooManyOriginsConfig.legacyUndeadEnabled && statusEffect == TMOEffects.ZOMBIFYING) {
                cir.setReturnValue(false);
            }
        }
    }
}
