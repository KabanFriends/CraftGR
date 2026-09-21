package sh.kaban.craftgr.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import sh.kaban.craftgr.CraftGR;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> CraftGR.getInstance().getConfig().createScreen(screen);
    }
}
