package io.github.kabanfriends.craftgr.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    private PoseStack poseStack = new PoseStack();

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"), ordinal = 0, method = "render")
    public PoseStack onScreenMatrixCreate(PoseStack poseStack) {
        this.poseStack = poseStack;
        return poseStack;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", shift = At.Shift.AFTER), method = "render")
    public void onRenderScreen(CallbackInfo i) {
        int mouseX = (int) (CraftGR.MC.mouseHandler.xpos() * (double) CraftGR.MC.getWindow().getGuiScaledWidth() / (double) CraftGR.MC.getWindow().getWidth());
        int mouseY = (int) (CraftGR.MC.mouseHandler.ypos() * (double) CraftGR.MC.getWindow().getGuiScaledHeight() / (double) CraftGR.MC.getWindow().getHeight());

        OverlayHandler.renderAll(this.poseStack, mouseX, mouseY);
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"), ordinal = 0, method = "render")
    public PoseStack onHudMatrixCreate(PoseStack poseStack) {
        this.poseStack = poseStack;
        return poseStack;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", shift = At.Shift.AFTER), method = "render")
    public void onRenderHud(CallbackInfo i) {
        if (CraftGR.MC.screen == null) {
            OverlayHandler.renderAll(this.poseStack, 0, 0);
        }
    }

}
