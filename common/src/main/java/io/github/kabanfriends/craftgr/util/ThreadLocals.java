package io.github.kabanfriends.craftgr.util;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public class ThreadLocals {

    public static final ThreadLocal<Boolean> PNG_INFO_BYPASS_VALIDATION = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> RADIO_OPTION_CONTAINER_ADDED = new ThreadLocal<>();
    public static final ThreadLocal<ScreenRectangle> SCISSOR_CURRENT_RECTANGLE = new ThreadLocal<>();
}
