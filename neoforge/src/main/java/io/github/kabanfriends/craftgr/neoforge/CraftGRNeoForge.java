package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.neoforge.events.*;
import io.github.kabanfriends.craftgr.neoforge.platform.NeoForgePlatform;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CraftGR.MOD_ID)
public class CraftGRNeoForge {

    public CraftGRNeoForge() {
        CraftGR.init(new NeoForgePlatform(Platform.PlatformType.FORGE));

        NeoForge.EVENT_BUS.register(new OverlayEvents());
        NeoForge.EVENT_BUS.register(new KeybindEvents());
        NeoForge.EVENT_BUS.register(new TickEvents());

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> GRConfig.getConfigScreen(screen)));
    }

}
