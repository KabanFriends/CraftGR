package io.github.kabanfriends.craftgr.fabric;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfigOptions;
import io.github.kabanfriends.craftgr.fabric.config.GRConfigFabric;
import io.github.kabanfriends.craftgr.fabric.config.builder.GRConfigBuilderFabric;
import io.github.kabanfriends.craftgr.fabric.platform.FabricPlatform;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.fabricmc.api.ModInitializer;

public class CraftGRFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GRConfigOptions.init(new GRConfigBuilderFabric());
        CraftGR.setConfig(new GRConfigFabric());
        CraftGR.init(new FabricPlatform(Platform.PlatformType.FABRIC));
    }
}
