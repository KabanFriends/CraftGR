package io.github.kabanfriends.craftgr.forge.event;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OverlayEvents {

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && CraftGR.MC.screen == null) {
            OverlayHandler.renderAll(event.getPoseStack(), 0, 0);
        }
    }

    @SubscribeEvent
    public void renderScreen(ScreenEvent.DrawScreenEvent.Post event) {
        OverlayHandler.renderAll(event.getPoseStack(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseClickedEvent.Pre event) {
        event.setCanceled(OverlayHandler.clickPressAll((int)event.getMouseX(), (int)event.getMouseY()));
    }

}
