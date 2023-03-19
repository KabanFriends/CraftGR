package io.github.kabanfriends.craftgr.fabric.platform;

import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.isxander.yacl.gui.RequireRestartScreen;
import dev.isxander.yacl.gui.YACLScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform extends Platform {

    public FabricPlatform(Platform.PlatformType type) {
        super(type);
    }

    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public String getModVersion(String id) {
        return FabricLoader.getInstance().getModContainer(id).get().getMetadata().getVersion().getFriendlyString();
    }

    public boolean isInModMenu() {
        if (CraftGR.getPlatform().isModLoaded("modmenu")) {
            return CraftGR.MC.screen instanceof ModsScreen;
        }
        return false;
    }

    public void openConfigScreen() {
        CraftGR.MC.setScreen(CraftGR.getConfig().getConfigScreen(CraftGR.MC.screen));
    }

    public boolean hasConfigMod() {
        if (isModLoaded("yet-another-config-lib")) {
            return true;
        }
        return false;
    }

    public boolean isInConfigScreen() {
        if (CraftGR.MC.screen instanceof YACLScreen || CraftGR.MC.screen instanceof RequireRestartScreen) {
            return true;
        }
        return false;
    }
}
