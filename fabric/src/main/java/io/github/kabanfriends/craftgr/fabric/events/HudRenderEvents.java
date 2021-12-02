package io.github.kabanfriends.craftgr.fabric.events;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class HudRenderEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((poseStack, delta) -> {
            if (CraftGR.MC.screen == null) OverlayHandler.renderAll(poseStack, 0, 0);
        });
    }

}
