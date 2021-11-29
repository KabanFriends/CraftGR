package io.github.kabanfriends.craftgr.forge.event;

import io.github.kabanfriends.craftgr.forge.keybinds.Keybinds;
import io.github.kabanfriends.craftgr.handler.KeyActionHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeybindEvents {

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (Keybinds.toggleMute.consumeClick()) KeyActionHandler.toggleMute();
    }

}
