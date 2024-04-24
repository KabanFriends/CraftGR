package io.github.kabanfriends.craftgr.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.util.ModUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(CraftGR.MOD_ID)
public class CraftGRNeoForge {

    public CraftGRNeoForge() {
        CraftGR.init(new NeoForgePlatform(Platform.PlatformType.FORGE));

        // Keybinds
        KeybindHandler.toggleMuteKey = new KeyMapping(
                "key.craftgr.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.category.craftgr"
        );

        // Events
        NeoForge.EVENT_BUS.register(new ClientEvents());

        // Config menu
        if (ModUtil.isConfigModAvailable()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, screen) -> GRConfig.getConfigScreen(screen));
        }
    }

}
