package sh.kaban.craftgr.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.Screen;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.util.IdentifierUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class Keybinds {

    private static final Keybind[] KEYBINDS = {
            new Keybind(
                    new KeyMapping(
                            "key.craftgr.toggle",
                            //? if > 26.2 {
                            InputConstants.Type.KEYBOARD,
                            //? } else
                            //InputConstants.Type.KEYSYM,
                            InputConstants.KEY_M,
                            //? if > 1.21.1 {
                            KeyMapping.Category.register(IdentifierUtil.thisMod("craftgr"))
                            //? } else
                            //"key.category.craftgr.craftgr"
                    ),
                    () -> CraftGR.getInstance().getRadio().toggle()
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
        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            for (Keybind keybind : KEYBINDS) {
                while (keybind.keyMapping().consumeClick()) {
                    keybind.runnable().run();
                }
            }
        }
    }

    record Keybind(KeyMapping keyMapping, Runnable runnable) {}
}
