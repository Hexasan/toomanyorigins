package com.github.merchantpug.toomanyorigins.mixin;

import com.github.merchantpug.toomanyorigins.registry.TMOEffects;
import com.github.merchantpug.toomanyorigins.TooManyOrigins;
import com.github.merchantpug.toomanyorigins.util.TooManyOriginsConfig;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.projectile.DragonFireballEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DragonFireballEntity.class)
public class DragonFireballEntityMixin {
    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;<init>(Lnet/minecraft/entity/effect/StatusEffect;II)V"))
    private StatusEffect swapOutDragonEffect(StatusEffect effect) {
        if (TooManyOriginsConfig.shouldFireballDamageUndead) {
            return effect = TMOEffects.END_FIRE;
        }
        return effect;
    }
}
