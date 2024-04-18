package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.TickEvent;

public class ClientEvents {

    public static boolean firstTicked;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.type == TickEvent.Type.CLIENT) {
            // Late init
            if (!firstTicked) {
                firstTicked = true;
                CraftGR.lateInit();
            }

            AudioPlayerHandler.getInstance().onClientTick();
            KeybindHandler.onClientTick();
        }
    }

    @SubscribeEvent
    public void renderHud(RenderGuiEvent.Post event) {
        if (CraftGR.MC.screen == null) {
            int mouseX = (int)(CraftGR.MC.mouseHandler.xpos() * (double)CraftGR.MC.getWindow().getGuiScaledWidth() / (double)CraftGR.MC.getWindow().getScreenWidth());
            int mouseY = (int)(CraftGR.MC.mouseHandler.ypos() * (double)CraftGR.MC.getWindow().getGuiScaledHeight() / (double)CraftGR.MC.getWindow().getScreenHeight());
            OverlayHandler.onRenderScreen(event.getGuiGraphics(), mouseX, mouseY);
        }
    }

    @SubscribeEvent
    public void renderScreen(ScreenEvent.Render.Post event) {
        OverlayHandler.onRenderScreen(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        event.setCanceled(OverlayHandler.mouseClickAll((int)event.getMouseX(), (int)event.getMouseY()));
    }
}
