package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;

public class ModUtil {

    public static boolean isConfigModAvailable() {
        return CraftGR.getInstance().getPlatform().isModLoaded("yet_another_config_lib_v3");
    }
}
