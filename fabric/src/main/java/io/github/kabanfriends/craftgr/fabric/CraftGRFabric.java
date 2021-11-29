package io.github.kabanfriends.craftgr.fabric;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.fabric.platform.FabricPlatform;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.api.ModInitializer;

public class CraftGRFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CraftGR.init(new FabricPlatform(Platform.PlatformType.FABRIC));
    }

}
