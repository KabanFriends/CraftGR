package io.github.kabanfriends.craftgr.platform;

import io.github.kabanfriends.craftgr.config.GRConfig;

public abstract class Platform {

    private PlatformType type;

    public Platform(PlatformType type) {
        this.type = type;
    }

    public PlatformType getPlatformType() {
        return this.type;
    }

    private GRConfig config;

    public abstract boolean isModLoaded(String id);

    public abstract String getModVersion(String id);

    public abstract boolean isInModMenu();

    public abstract void openConfigScreen();

    public abstract boolean hasConfigMod();

    public abstract boolean isInConfigScreen();

    public enum PlatformType {
        FABRIC,
        FORGE
    }

}
