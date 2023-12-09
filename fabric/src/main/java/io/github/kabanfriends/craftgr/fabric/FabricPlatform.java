package io.github.kabanfriends.craftgr.fabric;

import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

public class FabricPlatform extends Platform {

    public FabricPlatform(Platform.PlatformType type) {
        super(type);
    }

    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public String getModVersion(String id) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(id);
        return container.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    public boolean isInModMenu() {
        if (CraftGR.getPlatform().isModLoaded("modmenu")) {
            return CraftGR.MC.screen instanceof ModsScreen;
        }
        return false;
    }

    public void openConfigScreen() {
        CraftGR.MC.setScreen(GRConfig.getConfigScreen(CraftGR.MC.screen));
    }

}
