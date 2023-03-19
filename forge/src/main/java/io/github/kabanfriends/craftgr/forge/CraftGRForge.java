package io.github.kabanfriends.craftgr.forge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfigOptions;
import io.github.kabanfriends.craftgr.forge.config.GRConfigForge;
import io.github.kabanfriends.craftgr.forge.config.builder.GRConfigBuilderForge;
import io.github.kabanfriends.craftgr.forge.event.KeybindEvents;
import io.github.kabanfriends.craftgr.forge.event.OverlayEvents;
import io.github.kabanfriends.craftgr.forge.event.TickEvents;
import io.github.kabanfriends.craftgr.forge.platform.ForgePlatform;
import io.github.kabanfriends.craftgr.platform.Platform;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.ConfigScreenHandler;

@Mod(CraftGR.MOD_ID)
public class CraftGRForge {

    public CraftGRForge() {
        GRConfigOptions.init(new GRConfigBuilderForge());
        CraftGR.setConfig(new GRConfigForge());
        CraftGR.init(new ForgePlatform(Platform.PlatformType.FORGE));

        MinecraftForge.EVENT_BUS.register(new OverlayEvents());
        MinecraftForge.EVENT_BUS.register(new KeybindEvents());
        MinecraftForge.EVENT_BUS.register(new TickEvents());

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> CraftGR.getConfig().getConfigScreen(screen)));
    }
}
