package io.github.kabanfriends.craftgr.fabric.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.handler.KeyActionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Keybinds implements ClientModInitializer {

    private static KeyMapping toggleMute;

    @Override
    public void onInitializeClient() {
        //=====================
        // Initialization
        //=====================

        toggleMute = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.craftgr.togglemute",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.category.craftgr"
        ));

        //=====================
        // Events
        //=====================

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleMute.consumeClick()) KeyActionHandler.toggleMute();
        });

    }
}
