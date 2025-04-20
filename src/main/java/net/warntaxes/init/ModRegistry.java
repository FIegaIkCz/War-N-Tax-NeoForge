package net.warntaxes.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;
import net.warntaxes.WarNTaxesMod;

public class ModRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(Registries.SOUND_EVENT, WarNTaxesMod.MODID);

    public static final RegistryObject<SoundEvent> WAR_HORN =
        SOUNDS.register("war_horn", () -> SoundEvent.createVariableRangeEvent(WarNTaxesMod.rl("war_horn")));

    public static void register(net.neoforged.fml.event.lifecycle.FMLConstructModEvent event) {
        SOUNDS.register(event.getModEventBus());
    }
}
