package io.github.kabanfriends.craftgr.neoforge.events;

import io.github.kabanfriends.craftgr.CraftGR;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;

public class TickEvents {

    public static boolean firstTicked;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (!firstTicked && event.phase == TickEvent.Phase.START && event.type == TickEvent.Type.CLIENT) {
            firstTicked = true;
            CraftGR.lateInit();
        }
    }
}
