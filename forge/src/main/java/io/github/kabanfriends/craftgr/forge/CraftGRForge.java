package io.github.kabanfriends.craftgr.forge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.forge.event.KeybindEvents;
import io.github.kabanfriends.craftgr.forge.event.OverlayEvents;
import io.github.kabanfriends.craftgr.forge.keybinds.Keybinds;
import io.github.kabanfriends.craftgr.forge.platform.ForgePlatform;
import io.github.kabanfriends.craftgr.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.ConfigScreenHandler;

@Mod(CraftGR.MOD_ID)
public class CraftGRForge {

    public CraftGRForge() {
        CraftGR.init(new ForgePlatform(Platform.PlatformType.FORGE));

        MinecraftForge.EVENT_BUS.register(new OverlayEvents());
        MinecraftForge.EVENT_BUS.register(new KeybindEvents());

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> AutoConfig.getConfigScreen(GRConfig.class, screen).get()));
    }

}
