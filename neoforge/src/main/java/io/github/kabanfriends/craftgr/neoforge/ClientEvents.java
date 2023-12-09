package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.handler.KeybindHandler;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.neoforged.bus.api.SubscribeEvent;
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

            AudioPlayerHandler.getInstance().tick();
            KeybindHandler.tick();
        }
    }

    @SubscribeEvent
    public void clickScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        event.setCanceled(OverlayHandler.clickPressAll((int)event.getMouseX(), (int)event.getMouseY()));
    }
}
