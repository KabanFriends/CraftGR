package io.github.kabanfriends.craftgr.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.minecraft.client.KeyMapping;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
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
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> GRConfig.getConfigScreen(screen)));
    }

}
