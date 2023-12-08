package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.render.overlay.Overlay;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class OverlayHandler {

    private static List<Overlay> overlayList = new ArrayList<>();

    public static void addOverlay(Overlay overlay) {
        overlayList.add(overlay);
    }

    public static void render(Overlay overlay, GuiGraphics graphics, int mouseX, int mouseY) {
        overlay.render(graphics, mouseX, mouseY);
    }

    public static void renderAll(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Overlay overlay : overlayList) {
            render(overlay, graphics, mouseX, mouseY);
        }
    }

    public static boolean clickPress(Overlay overlay, int mouseX, int mouseY) {
        return overlay.onMouseClick(mouseX, mouseY);
    }

    public static boolean clickPressAll(int mouseX, int mouseY) {
        boolean cancelled = false;
        for (Overlay overlay : overlayList) {
            if (!clickPress(overlay, mouseX, mouseY)) cancelled = true;
        }
        return cancelled;
    }
}
