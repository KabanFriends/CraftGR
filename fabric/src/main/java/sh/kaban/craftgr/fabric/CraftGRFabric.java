package sh.kaban.craftgr.fabric;

import net.fabricmc.api.ClientModInitializer;
//? if > 1.21.11 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? } else
//import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import sh.kaban.craftgr.CraftGR;

public class CraftGRFabric implements ClientModInitializer {

    private CraftGR craftGR;

    @Override
    public void onInitializeClient() {
        craftGR = new CraftGR(new FabricPlatformAdapter(Minecraft.getInstance()));

        // Keybinds
        for (KeyMapping keyMapping : craftGR.getKeybinds().getKeyMappings()) {
            //? if > 1.21.11 {
            KeyMappingHelper.registerKeyMapping(keyMapping);
            //? } else
            //KeyBindingHelper.registerKeyBinding(keyMapping);
        }

        // Events
        FabricEvents.setup();
    }
}
