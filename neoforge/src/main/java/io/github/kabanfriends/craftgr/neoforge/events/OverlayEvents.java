package io.github.kabanfriends.craftgr.neoforge.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class OverlayEvents {

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        event.setCanceled(OverlayHandler.clickPressAll((int)event.getMouseX(), (int)event.getMouseY()));
    }
}
