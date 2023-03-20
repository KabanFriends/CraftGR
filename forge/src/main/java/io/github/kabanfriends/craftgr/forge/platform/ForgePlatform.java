package io.github.kabanfriends.craftgr.forge.platform;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.platform.Platform;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.client.gui.ModListScreen;

public class ForgePlatform extends Platform {

    public ForgePlatform(PlatformType type) {
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
        CraftGR.MC.setScreen(CraftGR.getConfig().getConfigScreen(CraftGR.MC.screen));
    }

    public boolean isInConfigScreen() {
        if (!isModLoaded("cloth-config") && !isModLoaded("cloth_config")) {
            return false;
        }
        if (CraftGR.MC.screen instanceof ConfigScreen) {
            return true;
        }
        return false;
    }
}
