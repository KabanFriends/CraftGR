package io.github.kabanfriends.craftgr.fabric.platform;

import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform extends Platform {

    public FabricPlatform(Platform.PlatformType type) {
        super(type);
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        if (CraftGR.PLATFORM.isModLoaded("modmenu")) {
            return CraftGR.MC.screen instanceof ModsScreen;
        }
        return false;
    }

    @Override
    public void openConfigScreen() {
        CraftGR.MC.setScreen(AutoConfig.getConfigScreen(GRConfig.class, CraftGR.MC.screen).get());
    }

}
