package io.github.kabanfriends.craftgr.forge.event;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OverlayEvents {

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        event.setCanceled(OverlayHandler.clickPressAll((int)event.getMouseX(), (int)event.getMouseY()));
    }
}
