package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

public class NeoForgeEvents {

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        CraftGR.getInstance().clientEvents().onClientStart();
    }

    @SubscribeEvent
    public void tick(ClientTickEvent.Pre event) {
        CraftGR.getInstance().clientEvents().onClientTick();
    }

    @SubscribeEvent
    public void renderHud(RenderGuiEvent.Post event) {
        Minecraft minecraft = CraftGR.getInstance().getMinecraft();
        if (minecraft.screen == null) {
            int mouseX = (int)(minecraft.mouseHandler.xpos() * (double)minecraft.getWindow().getGuiScaledWidth() / (double)minecraft.getWindow().getScreenWidth());
            int mouseY = (int)(minecraft.mouseHandler.ypos() * (double)minecraft.getWindow().getGuiScaledHeight() / (double)minecraft.getWindow().getScreenHeight());
            CraftGR.getInstance().clientEvents().onGameRender(event.getGuiGraphics(), mouseX, mouseY);
        }
    }

    @SubscribeEvent
    public void renderScreen(ScreenEvent.Render.Post event) {
        CraftGR.getInstance().clientEvents().onGameRender(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != 0) {
            return;
        }
        event.setCanceled(CraftGR.getInstance().clientEvents().onMouseClick((int) event.getMouseX(), (int) event.getMouseY()));
    }

    @SubscribeEvent
    public void onGameShutdown(GameShuttingDownEvent event) {
        CraftGR.getInstance().clientEvents().onClientStop();
    }
}
