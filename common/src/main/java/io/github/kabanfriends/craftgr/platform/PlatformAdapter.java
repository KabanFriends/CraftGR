package io.github.kabanfriends.craftgr.platform;

public interface PlatformAdapter {

    String getModVersion();

    boolean isModLoaded(String id);

    boolean isInModMenu();
}
