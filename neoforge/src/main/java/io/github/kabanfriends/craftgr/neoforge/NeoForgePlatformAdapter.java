package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.generated.ModConstants;
import io.github.kabanfriends.craftgr.platform.PlatformAdapter;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;

public class NeoForgePlatformAdapter implements PlatformAdapter {

    @Override
    public String getModVersion() {
        //noinspection OptionalGetWithoutIsPresent
        return ModList.get().getModContainerById(ModConstants.MOD_ID).get().getModInfo().getVersion().toString();
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        return Minecraft.getInstance().gui.screen() instanceof ModListScreen;
    }
}
