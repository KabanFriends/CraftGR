package io.github.kabanfriends.craftgr.fabric.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public class OverlayEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //Rendering in HUD
        HudRenderCallback.EVENT.register((poseStack, delta) -> {
            if (CraftGR.MC.screen == null) OverlayHandler.renderAll(poseStack, 0, 0);
        });

        //Screen events
        ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.afterRender(screen).register(((eventScreen, poseStack, mouseX, mouseY, tickDelta) -> OverlayHandler.renderAll(poseStack, mouseX, mouseY)));
            ScreenMouseEvents.allowMouseClick(screen).register(((eventScreen, mouseX, mouseY, button) -> !OverlayHandler.clickPressAll((int)mouseX, (int)mouseY)));
        }));
    }

}
