package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = CraftGR.MOD_ID)
public class GRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int volume = 50;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public SongInfoOverlay.OverlayVisibility overlayVisibility = SongInfoOverlay.OverlayVisibility.MENU;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public SongInfoOverlay.OverlayPosition overlayPosition = SongInfoOverlay.OverlayPosition.TOP_RIGHT;

    @ConfigEntry.Gui.Tooltip
    public boolean hideAlbumArt = false;

    @ConfigEntry.Gui.Tooltip
    public boolean openAlbum = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int overlayBgColor = 0x632279;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int overlayBarColor = 0xa096AE;

    @ConfigEntry.Gui.Tooltip
    public float overlayScale = 1.0f;

    @ConfigEntry.Gui.CollapsibleObject
    public URLConfig url = new URLConfig();

    @Config(name = "advanced")
    public static class URLConfig implements ConfigData {

        @ConfigEntry.Gui.Tooltip
        public String streamURL = "https://stream.gensokyoradio.net/1/";

        @ConfigEntry.Gui.Tooltip
        public String infoJsonURL = "https://gensokyoradio.net/api/station/playing/";

        @ConfigEntry.Gui.Tooltip
        public String albumArtURL = "https://gensokyoradio.net/images/albums/500/";

    }

    public static GRConfig getConfig() {
        return AutoConfig.getConfigHolder(GRConfig.class).getConfig();
    }

}
