package io.github.kabanfriends.craftgr.fabric;

import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public class FabricPlatform extends Platform {

    public FabricPlatform(Minecraft minecraft) {
        super(minecraft, PlatformType.FABRIC);
    }

    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public String getModVersion(String id) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(id);
        return container.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    public boolean isInModMenu() {
        if (isModLoaded("modmenu")) {
            return getMinecraft().screen instanceof ModsScreen;
        }
        return false;
    }
}
