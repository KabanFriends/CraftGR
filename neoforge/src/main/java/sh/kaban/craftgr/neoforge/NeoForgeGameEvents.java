package sh.kaban.craftgr.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import sh.kaban.craftgr.CraftGR;

public class NeoForgeGameEvents {

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        CraftGR.getInstance().clientEvents().onClientTick();
    }

    @SubscribeEvent
    public static void renderHud(RenderGuiEvent.Post event) {
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();

        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            int mouseX = (int) (mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
            int mouseY = (int) (mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
            CraftGR.getInstance().clientEvents().onGameRender(event.getGuiGraphics(), mouseX, mouseY);
        }
    }

    @SubscribeEvent
    public static void renderScreen(ScreenEvent.Render.Post event) {
        CraftGR.getInstance().clientEvents().onGameRender(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public static void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != InputConstants.MOUSE_BUTTON_LEFT) {
            return;
        }
        event.setCanceled(!CraftGR.getInstance().clientEvents().onMouseClick((int) event.getMouseX(), (int) event.getMouseY()));
    }

    @SubscribeEvent
    public static void onGameShutdown(GameShuttingDownEvent event) {
        CraftGR.getInstance().clientEvents().onClientStop();
    }
}
