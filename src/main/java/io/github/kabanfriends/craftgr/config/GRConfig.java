package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.awt.*;

@Config(name = CraftGR.MOD_ID)
public class GRConfig implements ConfigData {

    public static GRConfig getConfig() {
        return AutoConfig.getConfigHolder(GRConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip
    public String streamURL = "https://stream.gensokyoradio.net/1/";

    @ConfigEntry.Gui.Tooltip
    public String songInfoURL = "https://gensokyoradio.net/xml/";

    @ConfigEntry.Gui.Tooltip
    public String albumArtURL = "https://gensokyoradio.net/images/albums/500/";

    @ConfigEntry.Gui.Tooltip
    public SongInfoOverlay.OverlayPosition overlayPosition = SongInfoOverlay.OverlayPosition.TOP_RIGHT;

    @ConfigEntry.Gui.Tooltip
    public boolean hideAlbumArt = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int overlayBgColor = 0x632279;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int overlayBarColor = 0xa096AE;

    @ConfigEntry.Gui.Tooltip
    public float overlayScale = 1.0f;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int volume = 50;
}
