package io.github.kabanfriends.craftgr.platform;

public class Platform {

    private PlatformType type;

    public Platform(PlatformType type) {
        this.type = type;
    }

    public PlatformType getPlatform() {
        return this.type;
    }

    public boolean isModLoaded(String id) {
        throw new AssertionError();
    }

    public boolean isInForgeModMenu() {
        return false;
    }

    public enum PlatformType {
        FABRIC,
        FORGE
    }

}
