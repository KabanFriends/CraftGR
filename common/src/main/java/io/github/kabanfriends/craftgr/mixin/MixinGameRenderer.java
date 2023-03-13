package io.github.kabanfriends.craftgr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    private PoseStack poseStack;

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 1)
    public PoseStack onCreatePoseStack(PoseStack poseStack) {
        this.poseStack = poseStack;
        return poseStack;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 0))
    public void onRender(float f, long l, boolean bl, CallbackInfo ci) {
        if (!CraftGR.MC.options.hideGui || CraftGR.MC.screen != null) {
            if (CraftGR.renderSongOverlay && GRConfig.getValue("overlayVisibility") != SongInfoOverlay.OverlayVisibility.NONE) {
                CraftGR.MC.getProfiler().push("CraftGR Song Overlay");
                int mouseX = (int)(CraftGR.MC.mouseHandler.xpos() * (double)CraftGR.MC.getWindow().getGuiScaledWidth() / (double)CraftGR.MC.getWindow().getScreenWidth());
                int mouseY = (int)(CraftGR.MC.mouseHandler.ypos() * (double)CraftGR.MC.getWindow().getGuiScaledHeight() / (double)CraftGR.MC.getWindow().getScreenHeight());
                OverlayHandler.renderAll(poseStack, mouseX, mouseY);
                CraftGR.MC.getProfiler().pop();
            }
        }
    }
}
