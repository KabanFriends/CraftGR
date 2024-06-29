package io.github.kabanfriends.craftgr.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.ModUtil;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModUtil.isConfigModAvailable() ? (screen) -> CraftGR.getInstance().getConfig().createScreen(screen) : null;
    }
}
