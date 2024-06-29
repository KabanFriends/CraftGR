package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class NeoForgeModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> CraftGR.getInstance().clientEvents().onClientStart());
    }

    @SubscribeEvent
    public static void onRegisterKeymapping(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : CraftGR.getInstance().getKeybinds().getKeyMappings()) {
            event.register(keyMapping);
        }
    }
}
