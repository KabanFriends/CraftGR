package sh.kaban.craftgr.neoforge;

import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import sh.kaban.craftgr.CraftGR;

public class NeoForgeModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> CraftGR.getInstance().clientEvents().onClientStart());
    }

    @SubscribeEvent
    public static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : CraftGR.getInstance().getKeybinds().getKeyMappings()) {
            event.register(keyMapping);
        }
    }
}
