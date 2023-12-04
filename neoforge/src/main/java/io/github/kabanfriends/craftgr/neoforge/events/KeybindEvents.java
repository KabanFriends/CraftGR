package io.github.kabanfriends.craftgr.neoforge.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.KeyActionHandler;
import io.github.kabanfriends.craftgr.neoforge.keybinds.Keybinds;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;

public class KeybindEvents {

    private static boolean toggleMuteLastTick;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (CraftGR.MC.screen == null) {
            if (Keybinds.toggleMute.isDown() && !toggleMuteLastTick) KeyActionHandler.togglePlayback();
        }

        toggleMuteLastTick = Keybinds.toggleMute.isDown();
    }

}
