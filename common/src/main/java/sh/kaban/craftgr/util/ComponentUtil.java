package sh.kaban.craftgr.util;

import net.minecraft.network.chat.Component;
//? if > 1.21.1
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;

public final class ComponentUtil {

    //? if > 1.21.1 {
    private static final Style FONT_STYLE = Style.EMPTY.withFont(new FontDescription.Resource(IdentifierUtil.thisMod("icons")));
    //? } else
    //private static final Style FONT_STYLE = Style.EMPTY.withFont(IdentifierUtil.thisMod("icons"));

    public static final Component AUDIO_MUTED_ICON = Component.literal("M").withStyle(FONT_STYLE);
    public static final Component RECONNECT_ICON = Component.literal("R").withStyle(FONT_STYLE);

    // Private constructor to prevent instantiation
    private ComponentUtil() {
    }
}
