package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {

    @Inject(at = @At(value = "HEAD"), method = "wrapScreenError", cancellable = true)
    private static void onMouseClick(Runnable task, String errorTitle, String screenName, CallbackInfo info) {
        int mouseX = (int)(CraftGR.MC.mouse.getX() * (double)CraftGR.MC.getWindow().getScaledWidth() / (double)CraftGR.MC.getWindow().getWidth());
        int mouseY = (int)(CraftGR.MC.mouse.getY() * (double)CraftGR.MC.getWindow().getScaledHeight() / (double)CraftGR.MC.getWindow().getHeight());

        if ((errorTitle != null) && errorTitle.equals("mouseClicked event handler")) {
            OverlayHandler.clickPressAll(mouseX, mouseY, info);
        }
    }

}
