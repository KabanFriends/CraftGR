package sh.kaban.craftgr.event;

import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.config.ModConfig;
import sh.kaban.craftgr.song.SongProviderType;
//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;

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
        craftGR.shutdown();
    }

    public void onClientTick() {
        craftGR.getKeybinds().tick();
    }

    //? if > 1.21.11 {
    public void onGameRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    //? } else
    //public void onGameRender(GuiGraphics graphics, int mouseX, int mouseY) {
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
