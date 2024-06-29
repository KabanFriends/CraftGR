package io.github.kabanfriends.craftgr.fabric;

import io.github.kabanfriends.craftgr.CraftGR;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;

public class FabricEvents {

    public static void setup() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CraftGR.getInstance().clientEvents().onClientStart());

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> CraftGR.getInstance().clientEvents().onClientStop());

        ClientTickEvents.START_CLIENT_TICK.register(client -> CraftGR.getInstance().clientEvents().onClientTick());

        HudRenderCallback.EVENT.register((graphics, delta) -> {
            Minecraft minecraft = CraftGR.getInstance().getMinecraft();
            if (minecraft.screen == null) {
                int mouseX = (int)(minecraft.mouseHandler.xpos() * (double)minecraft.getWindow().getGuiScaledWidth() / (double)minecraft.getWindow().getScreenWidth());
                int mouseY = (int)(minecraft.mouseHandler.ypos() * (double)minecraft.getWindow().getGuiScaledHeight() / (double)minecraft.getWindow().getScreenHeight());
                CraftGR.getInstance().clientEvents().onGameRender(graphics, mouseX, mouseY);
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, initScreen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(initScreen).register((screen, mouseX, mouseY, button) -> {
                if (button != 0) {
                    return true;
                }
                return CraftGR.getInstance().clientEvents().onMouseClick((int)mouseX, (int)mouseY);
            });
            ScreenEvents.afterRender(initScreen).register((screen, graphics, mouseX, mouseY, tickDelta) -> CraftGR.getInstance().clientEvents().onGameRender(graphics, mouseX, mouseY));
        });
    }
}
