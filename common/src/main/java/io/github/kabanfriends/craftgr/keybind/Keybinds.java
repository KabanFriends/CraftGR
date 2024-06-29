package io.github.kabanfriends.craftgr.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    private static final Keybind[] KEYBINDS = {
            new Keybind(
                    new KeyMapping(
                            "key.craftgr.toggle",
                            InputConstants.Type.KEYSYM,
                            GLFW.GLFW_KEY_M,
                            "key.category.craftgr"
                    ),
                    () -> CraftGR.getInstance().getRadioStream().toggle()
            )
    };

    private final CraftGR craftGR;

    public Keybinds(CraftGR craftGR) {
        this.craftGR = craftGR;
    }

    public KeyMapping[] getKeyMappings() {
        KeyMapping[] keyMappings = new KeyMapping[KEYBINDS.length];
        for (int i = 0; i < KEYBINDS.length; i++) {
            keyMappings[i] = KEYBINDS[i].keyMapping();
        }
        return keyMappings;
    }

    public void tick() {
        if (craftGR.getMinecraft().screen == null) {
            for (Keybind keybind : KEYBINDS) {
                while (keybind.keyMapping().consumeClick()) {
                    keybind.runnable().run();
                }
            }
        }
    }

    record Keybind(KeyMapping keyMapping, Runnable runnable) {}
}
