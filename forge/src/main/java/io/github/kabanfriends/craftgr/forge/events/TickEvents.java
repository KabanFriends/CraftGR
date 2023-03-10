package io.github.kabanfriends.craftgr.forge.event;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickEvents {

    public static boolean firstTicked;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.type == TickEvent.Type.CLIENT && !firstTicked) {
            firstTicked = true;
            CraftGR.lateInit();
        }
    }
}
