package sh.kaban.craftgr.fabric;

import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.generated.ModConstants;
import sh.kaban.craftgr.platform.PlatformAdapter;

public class FabricPlatformAdapter implements PlatformAdapter {

    private final Minecraft minecraft;

    public FabricPlatformAdapter(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public String getModVersion() {
        //noinspection OptionalGetWithoutIsPresent
        return FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID).get().getMetadata().getVersion().toString();
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isInModMenu() {
        //? if > 1.21.11 {
        Screen screen = minecraft.gui.screen();
        //? } else
        //Screen screen = minecraft.screen;

        if (isModLoaded("modmenu")) {
            return screen instanceof ModsScreen;
        }
        return false;
    }
}
