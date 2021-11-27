package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    private MatrixStack matrix = new MatrixStack();

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"), ordinal = 0, method = "render")
    public MatrixStack onScreenMatrixCreate(MatrixStack matrix) {
        this.matrix = matrix;
        return matrix;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER), method = "render")
    public void onRenderScreen(CallbackInfo i) {
        if (GRConfig.getConfig().overlayVisibility == SongInfoOverlay.OverlayVisibility.NONE) return;
        if (GRConfig.getConfig().overlayVisibility == SongInfoOverlay.OverlayVisibility.CHAT && !(CraftGR.MC.currentScreen instanceof ChatScreen))
            return;

        int mouseX = (int) (CraftGR.MC.mouse.getX() * (double) CraftGR.MC.getWindow().getScaledWidth() / (double) CraftGR.MC.getWindow().getWidth());
        int mouseY = (int) (CraftGR.MC.mouse.getY() * (double) CraftGR.MC.getWindow().getScaledHeight() / (double) CraftGR.MC.getWindow().getHeight());

        OverlayHandler.renderAll(this.matrix, mouseX, mouseY);
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V"), ordinal = 0, method = "render")
    public MatrixStack onHudMatrixCreate(MatrixStack matrix) {
        this.matrix = matrix;
        return matrix;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", shift = At.Shift.AFTER), method = "render")
    public void onRenderHud(CallbackInfo i) {
        if (GRConfig.getConfig().overlayVisibility == SongInfoOverlay.OverlayVisibility.ALWAYS && CraftGR.MC.currentScreen == null) {
            int mouseX = (int) (CraftGR.MC.mouse.getX() * (double) CraftGR.MC.getWindow().getScaledWidth() / (double) CraftGR.MC.getWindow().getWidth());
            int mouseY = (int) (CraftGR.MC.mouse.getY() * (double) CraftGR.MC.getWindow().getScaledHeight() / (double) CraftGR.MC.getWindow().getHeight());

            OverlayHandler.renderAll(this.matrix, mouseX, mouseY);
        }
    }

}
