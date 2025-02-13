package io.github.kabanfriends.craftgr.fabric;

import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class FabricPlatform implements Platform {

    private final Minecraft minecraft;

    public FabricPlatform(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        if (isModLoaded("modmenu")) {
            return minecraft.screen instanceof ModsScreen;
        }
        return false;
    }
}
