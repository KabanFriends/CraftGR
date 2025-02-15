package io.github.kabanfriends.craftgr.event;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.overlay.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.SongProviderType;
import io.github.kabanfriends.craftgr.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.io.IOException;

public class ClientEvents {

    private final CraftGR craftGR;

    public ClientEvents(CraftGR craftGR) {
        this.craftGR = craftGR;
    }

    public void onClientStart() {
        craftGR.getSongInfoOverlay().setActive(true);
        craftGR.setSongProvider(ModConfig.<SongProviderType>get("songProvider").createProvider());
    }

    public void onClientStop() {
        craftGR.getRadio().stop(false);

        try {
            craftGR.getSongProvider().stop();
            craftGR.getHttpClient().close();
        } catch (IOException ignored)  {
        }
    }

    public void onClientTick() {
        craftGR.getKeybinds().tick();
    }

    public void onGameRender(GuiGraphics graphics, int mouseX, int mouseY) {
        /*
        if (craftGR.getRadio().getAudioPlayer() != null && craftGR.getRadio().getAudioPlayer().getFreqRenderer() != null) {
            double[] bands = craftGR.getRadio().getAudioPlayer().getFreqRenderer().calculateBandsNow();

            graphics.drawString(craftGR.getMinecraft().font, "" + bands.length, 10, 10, 0xFFFFFFFF);

            for (int i = 0; i < bands.length; i++) {
                int x = 10 + i * 5;
                int y = 20;
                int x2 = x + 5;
                int y2 = y + (int) (bands[i] * 100);

                RenderUtil.fill(graphics.pose(), x, y, x2, y2, 0xFFFFFFFF, 1.0f);
            }
        }
        */

        if (!shouldRenderOverlay(craftGR)) {
            return;
        }
        craftGR.getSongInfoOverlay().render(graphics, mouseX, mouseY);
    }

    public boolean onMouseClick(int mouseX, int mouseY) {
        if (!shouldRenderOverlay(craftGR)) {
            return true;
        }
        return craftGR.getSongInfoOverlay().mouseClick(mouseX, mouseY);
    }

    private static boolean shouldRenderOverlay(CraftGR craftGR) {
        SongInfoOverlay overlay = craftGR.getSongInfoOverlay();
        return overlay.isActive() && !craftGR.getMinecraft().options.hideGui && !craftGR.getMinecraft().getDebugOverlay().showDebugScreen();
    }
}
