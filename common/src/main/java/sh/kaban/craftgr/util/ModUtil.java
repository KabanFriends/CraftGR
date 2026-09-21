package sh.kaban.craftgr.util;

import sh.kaban.craftgr.CraftGR;

public class ModUtil {

    public static boolean isConfigModAvailable() {
        return CraftGR.getInstance().getPlatformAdapter().isModLoaded("yet_another_config_lib_v3");
    }
}
