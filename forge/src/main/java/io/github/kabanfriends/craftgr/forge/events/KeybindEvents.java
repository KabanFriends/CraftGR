package io.github.kabanfriends.craftgr.forge.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.forge.keybinds.Keybinds;
import io.github.kabanfriends.craftgr.handler.KeyActionHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
