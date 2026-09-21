package sh.kaban.craftgr.util;

import sh.kaban.craftgr.generated.ModConstants;
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
