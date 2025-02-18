package io.github.kabanfriends.craftgr.platform;

public interface PlatformAdapter {

    boolean isModLoaded(String id);

    boolean isInModMenu();
}
