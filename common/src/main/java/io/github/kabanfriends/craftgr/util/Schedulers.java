package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.generated.ModConstants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Schedulers {

    // Private constructor to prevent instantiation
    private Schedulers() {
    }

    public static ScheduledExecutorService newDaemonScheduler(String name) {
        return Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable, ModConstants.MOD_ID + "-" + name);
            thread.setDaemon(true);
            return thread;
        });
    }
}
