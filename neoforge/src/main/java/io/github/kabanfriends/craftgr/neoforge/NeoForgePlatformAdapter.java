package io.github.kabanfriends.craftgr.neoforge;

import io.github.kabanfriends.craftgr.platform.PlatformAdapter;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ModListScreen;

public class NeoForgePlatformAdapter implements PlatformAdapter {

    private final Minecraft minecraft;

    public NeoForgePlatformAdapter(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        return minecraft.screen instanceof ModListScreen;
    }
}
