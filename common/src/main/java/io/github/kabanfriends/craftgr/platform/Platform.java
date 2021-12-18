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
        throw new AssertionError("No platform specified!");
    }

    public boolean isInModMenu() {
        return false;
    }

    public void openConfigScreen() {
        throw new AssertionError("No platform specified!");
    }

    public enum PlatformType {
        FABRIC,
        FORGE
    }

}
