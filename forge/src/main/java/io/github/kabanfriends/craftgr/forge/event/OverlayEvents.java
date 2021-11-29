package io.github.kabanfriends.craftgr.forge.event;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OverlayEvents {

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        PoseStack poseStack = event.getMatrixStack();
        poseStack.pushPose();
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && CraftGR.MC.screen == null) {
            OverlayHandler.renderAll(event.getMatrixStack(), 0, 0);
        }
        poseStack.popPose();
    }

    @SubscribeEvent
    public void renderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        OverlayHandler.renderAll(event.getMatrixStack(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void clickScreen(GuiScreenEvent.MouseClickedEvent event) {
        event.setCanceled(OverlayHandler.clickPressAll((int)event.getMouseX(), (int)event.getMouseY()));
    }

}
