package io.github.kabanfriends.craftgr.neoforge.platform;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ModListScreen;

public class NeoForgePlatform extends Platform {

    public NeoForgePlatform(PlatformType type) {
        super(type);
    }

    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public String getModVersion(String id) {
        return ModList.get().getModContainerById(id).get().getModInfo().getVersion().toString();
    }

    public boolean isInModMenu() {
        return CraftGR.MC.screen instanceof ModListScreen;
    }

    public void openConfigScreen() {
        CraftGR.MC.setScreen(GRConfig.getConfigScreen(CraftGR.MC.screen));
    }

}
