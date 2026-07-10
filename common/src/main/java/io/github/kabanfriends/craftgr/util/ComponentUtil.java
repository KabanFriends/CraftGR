package io.github.kabanfriends.craftgr.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;

public final class ComponentUtil {

    private static final Style FONT_STYLE = Style.EMPTY.withFont(new FontDescription.Resource(IdentifierUtil.thisMod("icons")));

    public static final Component AUDIO_MUTED_ICON = Component.literal("M").withStyle(FONT_STYLE);
    public static final Component RECONNECT_ICON = Component.literal("R").withStyle(FONT_STYLE);

    // Private constructor to prevent instantiation
    private ComponentUtil() {
    }
}
