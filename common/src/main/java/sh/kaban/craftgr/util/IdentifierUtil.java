package sh.kaban.craftgr.util;

import sh.kaban.craftgr.generated.ModConstants;

public final class IdentifierUtil {

    // Private constructor to prevent instantiation
    private IdentifierUtil() {
    }

    //? if > 1.21.1 {
    public static net.minecraft.resources.Identifier thisMod(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, path);
    }
    //? } else {
    /*public static net.minecraft.resources.ResourceLocation thisMod(String path) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, path);
    }
    *///? }
}
