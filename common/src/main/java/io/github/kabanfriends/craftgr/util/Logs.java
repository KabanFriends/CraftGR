package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.generated.ModConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logs {

    // Private constructor to prevent instantiation
    private Logs() {
    }

    public static Logger logger() {
        return LoggerFactory.getLogger(ModConstants.MOD_NAME);
    }
}
