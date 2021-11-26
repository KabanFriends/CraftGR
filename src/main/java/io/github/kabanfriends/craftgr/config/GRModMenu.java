package io.github.kabanfriends.craftgr.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import me.shedaniel.autoconfig.AutoConfig;

public class GRModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(GRConfig.class, parent).get();
    }

    public static boolean isInModMenu() {
        return CraftGR.MC.currentScreen instanceof ModsScreen;
    }

}
