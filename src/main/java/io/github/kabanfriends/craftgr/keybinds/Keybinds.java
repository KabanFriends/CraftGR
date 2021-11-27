package io.github.kabanfriends.craftgr.keybinds;

import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.MessageUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds implements ClientModInitializer {

    private static KeyBinding toggleMute;

    @Override
    public void onInitializeClient() {
        //=====================
        // Initialization
        //=====================

        toggleMute = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.craftgr.togglemute",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.category.craftgr"
        ));

        //=====================
        // Events
        //=====================

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleMute.wasPressed()) {
                AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

                handler.player.muted = !AudioPlayerHandler.getInstance().player.muted;
                if (handler.player.muted) MessageUtil.sendTranslatableMessage("text.craftgr.message.muted");
                else MessageUtil.sendTranslatableMessage("text.craftgr.message.unmuted");

                handler.player.setVolume(1.0F);
            }
        });

    }
}
