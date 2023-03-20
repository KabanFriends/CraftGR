package io.github.kabanfriends.craftgr.fabric.platform;

import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.isxander.yacl.gui.RequireRestartScreen;
import dev.isxander.yacl.gui.YACLScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.fabric.config.GRConfigFabric;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;

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

    public boolean isInConfigScreen() {
        if (isModLoaded("yet-another-config-lib")) {
            return false;
        }
        if (CraftGR.MC.screen instanceof RequireRestartScreen) {
            return true;
        }
        if (CraftGR.MC.screen instanceof YACLScreen screen) {
            ComponentContents contents = screen.config.title().getContents();
            if (contents instanceof TranslatableContents translatable) {
                if (translatable.getKey().equals(GRConfigFabric.TITLE_KEY)) {
                    return true;
                }
            }
        }
        return false;
    }
}
