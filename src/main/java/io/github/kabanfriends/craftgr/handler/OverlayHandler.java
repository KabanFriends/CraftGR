package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.render.Overlay;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class OverlayHandler {

    private static List<Overlay> overlayList = new ArrayList<>();

    public static void addOverlay(Overlay overlay) {
        overlayList.add(overlay);
    }

    public static void render(MatrixStack matrix, int mouseX, int mouseY, Overlay overlay) {
        overlay.render(matrix, mouseX, mouseY);
    }

    public static void renderAll(MatrixStack matrix, int mouseX, int mouseY) {
        for (Overlay overlay : overlayList) {
            overlay.render(matrix, mouseX, mouseY);
        }
    }
}
