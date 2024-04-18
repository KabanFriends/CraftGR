package io.github.kabanfriends.craftgr.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CraftGRFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CraftGR.init(new FabricPlatform(Platform.PlatformType.FABRIC));

        // Keybinds
        KeybindHandler.toggleMuteKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.craftgr.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.category.craftgr"
        ));

        // Events
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CraftGR.lateInit());

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            AudioPlayerHandler.getInstance().onClientTick();
            KeybindHandler.onClientTick();
        });

        HudRenderCallback.EVENT.register((graphics, delta) -> {
            if (CraftGR.MC.screen == null) {
                int mouseX = (int)(CraftGR.MC.mouseHandler.xpos() * (double)CraftGR.MC.getWindow().getGuiScaledWidth() / (double)CraftGR.MC.getWindow().getScreenWidth());
                int mouseY = (int)(CraftGR.MC.mouseHandler.ypos() * (double)CraftGR.MC.getWindow().getGuiScaledHeight() / (double)CraftGR.MC.getWindow().getScreenHeight());
                OverlayHandler.onRenderScreen(graphics, mouseX, mouseY);
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, initScreen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(initScreen).register((screen, mouseX, mouseY, button) -> !OverlayHandler.mouseClickAll((int) mouseX, (int) mouseY));
            ScreenEvents.afterRender(initScreen).register((screen, graphics, mouseX, mouseY, tickDelta) -> OverlayHandler.onRenderScreen(graphics, mouseX, mouseY));
        });
    }
}
