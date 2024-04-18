package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.overlay.Overlay;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class OverlayHandler {

    private static List<Overlay> overlayList = new ArrayList<>();

    public static void addOverlay(Overlay overlay) {
        overlayList.add(overlay);
    }

    public static void renderAll(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Overlay overlay : overlayList) {
            overlay.render(graphics, mouseX, mouseY);
        }
    }

    public static boolean mouseClickAll(int mouseX, int mouseY) {
        boolean cancelled = false;
        for (Overlay overlay : overlayList) {
            if (!overlay.mouseClick(mouseX, mouseY)) cancelled = true;
        }
        return cancelled;
    }

    public static void onRenderScreen(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!CraftGR.MC.options.hideGui || CraftGR.MC.screen != null) {
            if (CraftGR.renderSongOverlay && GRConfig.getValue("overlayVisibility") != SongInfoOverlay.OverlayVisibility.NONE) {
                CraftGR.MC.getProfiler().push("CraftGR Song Overlay");
                OverlayHandler.renderAll(graphics, mouseX, mouseY);
                CraftGR.MC.getProfiler().pop();
            }
        }
    }
}
