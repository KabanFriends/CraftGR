package io.github.kabanfriends.craftgr.util;

import net.minecraft.client.Minecraft;

public class RenderUtil {

    private static final float UI_BASE_SCALE = 1.0f;

    public static float getUIScale(float uiScale) {
        double mcScale = Minecraft.getInstance().getWindow().getGuiScale();

        return (float) ((((double) UI_BASE_SCALE) * (((double) UI_BASE_SCALE) / mcScale)) * uiScale);
    }
}
