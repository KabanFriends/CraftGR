package io.github.kabanfriends.craftgr.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class CraftGRFabric implements ClientModInitializer {

    private CraftGR craftGR;

    @Override
    public void onInitializeClient() {
        craftGR = new CraftGR(new FabricPlatform(Minecraft.getInstance()));

        // Keybinds
        KeybindHandler.toggleMuteKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.craftgr.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.category.craftgr"
        ));

        // Events
        FabricEvents.setup();
    }
}
