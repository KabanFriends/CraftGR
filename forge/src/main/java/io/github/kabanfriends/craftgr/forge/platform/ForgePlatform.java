package io.github.kabanfriends.craftgr.forge.platform;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmlclient.gui.screen.ModListScreen;

public class ForgePlatform extends Platform {

    public ForgePlatform(PlatformType type) {
        super(type);
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isInForgeModMenu() {
        return CraftGR.MC.screen instanceof ModListScreen;
    }

}
