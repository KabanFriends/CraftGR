package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.platform.PlatformAdapter;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ModListScreen;

public class NeoForgePlatformAdapter implements PlatformAdapter {

    @Override
    public String getModVersion() {
        //noinspection OptionalGetWithoutIsPresent
        return ModList.get().getModContainerById(CraftGR.MOD_ID).get().getModInfo().getVersion().toString();
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        return Minecraft.getInstance().screen instanceof ModListScreen;
    }
}
