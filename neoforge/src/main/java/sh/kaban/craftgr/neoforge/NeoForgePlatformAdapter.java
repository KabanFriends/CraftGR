package sh.kaban.craftgr.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModList;
//? if > 1.21.11 {
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;
//? } else
//import net.neoforged.neoforge.client.gui.ModListScreen;
import sh.kaban.craftgr.generated.ModConstants;
import sh.kaban.craftgr.platform.PlatformAdapter;

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
        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        return screen instanceof ModListScreen;
    }
}
