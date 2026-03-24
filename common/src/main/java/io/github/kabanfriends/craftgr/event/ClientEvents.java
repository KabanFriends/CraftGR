package io.github.kabanfriends.craftgr.event;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.song.SongProviderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
        craftGR.getSongProvider().stop();
    }

    public void onClientTick() {
        craftGR.getKeybinds().tick();
    }

    public void onGameRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!craftGR.getSongInfoOverlay().shouldRender()) {
            return;
        }
        craftGR.getSongInfoOverlay().render(graphics, mouseX, mouseY);
    }

    public boolean onMouseClick(int mouseX, int mouseY) {
        if (!craftGR.getSongInfoOverlay().shouldRender()) {
            return true;
        }
        return craftGR.getSongInfoOverlay().mouseClick(mouseX, mouseY);
    }
}
