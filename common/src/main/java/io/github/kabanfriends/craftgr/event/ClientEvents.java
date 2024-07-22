package io.github.kabanfriends.craftgr.event;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.RadioStream;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.overlay.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.SongProviderType;
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
        RadioStream stream = craftGR.getRadioStream();
        stream.disconnect();

        try {
            craftGR.getSongProvider().stop();
            craftGR.getHttpClient().close();
        } catch (IOException ignored)  {
        }
    }

    public void onClientTick() {
        craftGR.getRadioStream().tick();
        craftGR.getKeybinds().tick();
    }

    public void onGameRender(GuiGraphics graphics, int mouseX, int mouseY) {
        if (shouldRenderOverlay(craftGR)) {
            return;
        }
        craftGR.getSongInfoOverlay().render(graphics, mouseX, mouseY);
    }

    public boolean onMouseClick(int mouseX, int mouseY) {
        if (shouldRenderOverlay(craftGR)) {
            return true;
        }
        return craftGR.getSongInfoOverlay().mouseClick(mouseX, mouseY);
    }

    private static boolean shouldRenderOverlay(CraftGR craftGR) {
        SongInfoOverlay overlay = craftGR.getSongInfoOverlay();
        return overlay.isActive() && !craftGR.getMinecraft().options.hideGui && !craftGR.getMinecraft().getDebugOverlay().showDebugScreen();
    }
}
