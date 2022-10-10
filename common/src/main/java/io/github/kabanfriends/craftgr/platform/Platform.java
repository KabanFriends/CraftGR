package io.github.kabanfriends.craftgr.platform;

public abstract class Platform {

    private PlatformType type;

    public Platform(PlatformType type) {
        this.type = type;
    }

    public PlatformType getPlatformType() {
        return this.type;
    }

    public abstract boolean isModLoaded(String id);

    public abstract String getModVersion(String id);

    public abstract boolean isInModMenu();

    public abstract void openConfigScreen();

    public enum PlatformType {
        FABRIC,
        FORGE
    }

}
