package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.render.Overlay;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

public class OverlayHandler {

    private static List<Overlay> overlayList = new ArrayList<>();

    public static void addOverlay(Overlay overlay) {
        overlayList.add(overlay);
    }

    public static void render(Overlay overlay, MatrixStack matrix, int mouseX, int mouseY) {
        overlay.render(matrix, mouseX, mouseY);
    }

    public static void renderAll(MatrixStack matrix, int mouseX, int mouseY) {
        for (Overlay overlay : overlayList) {
            render(overlay, matrix, mouseX, mouseY);
        }
    }

    public static void clickPress(Overlay overlay, int mouseX, int mouseY, CallbackInfo info) {
        overlay.onMouseClick(mouseX, mouseY, info);
    }

    public static void clickPressAll(int mouseX, int mouseY, CallbackInfo info) {
        for (Overlay overlay : overlayList) {
            clickPress(overlay, mouseX, mouseY, info);
        }
    }
}
