package io.github.kabanfriends.craftgr.fabric.event;

import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public class OverlayEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(screen).register(((eventScreen, mouseX, mouseY, button) -> !OverlayHandler.clickPressAll((int)mouseX, (int)mouseY)));
        }));
    }
}
