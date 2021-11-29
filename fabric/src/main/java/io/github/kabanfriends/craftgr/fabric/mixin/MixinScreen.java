package io.github.kabanfriends.craftgr.fabric.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {

    @Inject(at = @At(value = "HEAD"), method = "wrapScreenError", cancellable = true)
    private static void onMouseClick(Runnable task, String errorTitle, String screenName, CallbackInfo info) {
        int mouseX = (int) (CraftGR.MC.mouseHandler.xpos() * (double) CraftGR.MC.getWindow().getGuiScaledWidth() / (double) CraftGR.MC.getWindow().getWidth());
        int mouseY = (int) (CraftGR.MC.mouseHandler.ypos() * (double) CraftGR.MC.getWindow().getGuiScaledHeight() / (double) CraftGR.MC.getWindow().getHeight());

        if ((errorTitle != null) && errorTitle.equals("mouseClicked event handler")) {
            boolean cancelled = OverlayHandler.clickPressAll(mouseX, mouseY);
            if (cancelled) info.cancel();
        }
    }

}
