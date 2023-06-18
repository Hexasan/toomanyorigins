package net.merchantpug.toomanyorigins.registry;

import net.merchantpug.toomanyorigins.TooManyOrigins;
import net.merchantpug.toomanyorigins.registry.services.RegistrationProvider;
import net.merchantpug.toomanyorigins.registry.services.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class TMOSounds {
    private static final RegistrationProvider<SoundEvent> SOUND_EVENT = RegistrationProvider.get(Registries.SOUND_EVENT, TooManyOrigins.MOD_ID);

    public static final RegistryObject<SoundEvent> ORIGIN_HARE_DASH = register("origin.hare.dash", () -> SoundEvent.createVariableRangeEvent(TooManyOrigins.asResource("origin.hare.dash")));

    public static void register() {
    }

    public static <T extends SoundEvent> RegistryObject<T> register(String name, Supplier<T> soundEvent) {
        return SOUND_EVENT.register(name, soundEvent);
    }
}
