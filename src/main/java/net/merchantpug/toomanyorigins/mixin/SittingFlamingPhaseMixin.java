package net.merchantpug.toomanyorigins.mixin;

import net.merchantpug.toomanyorigins.registry.TMOEffects;
import net.merchantpug.toomanyorigins.util.TooManyOriginsConfig;
import net.minecraft.entity.boss.dragon.phase.SittingFlamingPhase;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SittingFlamingPhase.class)
public class SittingFlamingPhaseMixin {
    @ModifyArg(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;<init>(Lnet/minecraft/entity/effect/StatusEffect;)V"))
    private StatusEffect statusEffect(StatusEffect effect) {
        if (TooManyOriginsConfig.shouldFireballDamageUndead) {
            return effect = TMOEffects.END_FIRE;
        }
        return effect;
    }
}