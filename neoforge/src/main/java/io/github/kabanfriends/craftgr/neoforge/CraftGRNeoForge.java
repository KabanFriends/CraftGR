package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CraftGR.MOD_ID)
public class CraftGRNeoForge {

    private final CraftGR craftGR;

    public CraftGRNeoForge(IEventBus modBus) {
        craftGR = new CraftGR(new NeoForgePlatformAdapter(Minecraft.getInstance()));

        // Events
        modBus.register(NeoForgeModEvents.class);
        NeoForge.EVENT_BUS.register(NeoForgeGameEvents.class);

        // Config menu
        if (ModUtil.isConfigModAvailable()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, screen) -> craftGR.getConfig().createScreen(screen));
        }
    }
}
