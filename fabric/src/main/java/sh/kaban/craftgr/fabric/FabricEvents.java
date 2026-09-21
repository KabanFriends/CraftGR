package sh.kaban.craftgr.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if > 1.21.1 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//? } else
//import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.util.IdentifierUtil;

public class FabricEvents {

    public static void setup() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CraftGR.getInstance().clientEvents().onClientStart());

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> CraftGR.getInstance().clientEvents().onClientStop());

        ClientTickEvents.START_CLIENT_TICK.register(client -> CraftGR.getInstance().clientEvents().onClientTick());

        //? if > 1.21.1 {
        HudElementRegistry.addFirst(IdentifierUtil.thisMod("overlay"), (graphics, delta) -> {
        //? } else
        //HudRenderCallback.EVENT.register((graphics, delta) -> {
            MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
            Window window = Minecraft.getInstance().getWindow();

            //? if > 1.21.11 {
            Screen screen = Minecraft.getInstance().gui.screen();
            //? } else
            //Screen screen = Minecraft.getInstance().screen;

            if (screen == null) {
                int mouseX = (int) (mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
                int mouseY = (int) (mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
                CraftGR.getInstance().clientEvents().onGameRender(graphics, mouseX, mouseY);
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, initScreen, scaledWidth, scaledHeight) -> {
            //? if > 1.21.1 {
            ScreenMouseEvents.allowMouseClick(initScreen).register((screen, event) -> {
                if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
                    return true;
                }
                return CraftGR.getInstance().clientEvents().onMouseClick((int) event.x(), (int) event.y());
            });
            //? } else {
            /*ScreenMouseEvents.allowMouseClick(initScreen).register((screen, mouseX, mouseY, button) -> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) {
                    return true;
                }
                return CraftGR.getInstance().clientEvents().onMouseClick((int) mouseX, (int) mouseY);
            });
            *///? }
            //? if > 1.21.11 {
            ScreenEvents.afterExtract(initScreen).register((screen, graphics, mouseX, mouseY, tickDelta) -> CraftGR.getInstance().clientEvents().onGameRender(graphics, mouseX, mouseY));
            //? } else
            //ScreenEvents.afterRender(initScreen).register((screen, graphics, mouseX, mouseY, tickDelta) -> CraftGR.getInstance().clientEvents().onGameRender(graphics, mouseX, mouseY));
        });
    }
}
