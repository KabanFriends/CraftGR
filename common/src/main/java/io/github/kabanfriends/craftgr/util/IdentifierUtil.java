package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.generated.ModConstants;
import net.minecraft.resources.Identifier;

public final class IdentifierUtil {

    // Private constructor to prevent instantiation
    private IdentifierUtil() {
    }

    public static Identifier thisMod(String path) {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, path);
    }
}
