package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ModListScreen;

import java.util.Optional;

public class NeoForgePlatform extends Platform {

    public NeoForgePlatform(PlatformType type) {
        super(type);
    }

    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public String getModVersion(String id) {
        Optional<? extends ModContainer> container = ModList.get().getModContainerById(id);
        return container.map(mod -> mod.getModInfo().getVersion().toString()).orElse(null);
    }

    public boolean isInModMenu() {
        return CraftGR.MC.screen instanceof ModListScreen;
    }

    public void openConfigScreen() {
        CraftGR.MC.setScreen(GRConfig.getConfigScreen(CraftGR.MC.screen));
    }

}
