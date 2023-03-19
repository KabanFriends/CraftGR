package io.github.kabanfriends.craftgr.fabric.event;

import io.github.kabanfriends.craftgr.CraftGR;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class LifecycleEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CraftGR.lateInit());
    }
}
