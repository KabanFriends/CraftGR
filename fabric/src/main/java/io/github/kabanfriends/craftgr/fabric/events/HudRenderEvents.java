package io.github.kabanfriends.craftgr.fabric.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class HudRenderEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((poseStack, delta) -> {
            if (CraftGR.MC.screen == null) OverlayHandler.renderAll(poseStack, 0, 0);
        });

        ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.afterRender(screen).register(((renderScreen, poseStack, mouseX, mouseY, tickDelta) -> {
                OverlayHandler.renderAll(poseStack, mouseX, mouseY);
            }));
        }));
    }

}
