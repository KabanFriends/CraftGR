package io.github.kabanfriends.craftgr.forge.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static final KeyMapping toggleMute = new KeyMapping(
            "key.craftgr.togglemute",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "key.category.craftgr"
    );;

    public static void initialize() {
        ClientRegistry.registerKeyBinding(toggleMute);
    }

}
