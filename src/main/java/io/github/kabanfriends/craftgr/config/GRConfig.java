package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.CraftGR;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

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
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int volume = 50;
}
