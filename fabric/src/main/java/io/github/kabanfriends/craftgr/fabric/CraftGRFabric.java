package io.github.kabanfriends.craftgr.fabric;

import io.github.kabanfriends.craftgr.CraftGR;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class CraftGRFabric implements ClientModInitializer {

    private CraftGR craftGR;

    @Override
    public void onInitializeClient() {
        craftGR = new CraftGR(new FabricPlatformAdapter(Minecraft.getInstance()));

        // Keybinds
        for (KeyMapping keyMapping : craftGR.getKeybinds().getKeyMappings()) {
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }

        // Events
        FabricEvents.setup();
    }
}
