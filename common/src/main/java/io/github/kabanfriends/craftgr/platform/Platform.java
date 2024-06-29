package io.github.kabanfriends.craftgr.platform;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public abstract class Platform {

    private final Minecraft minecraft;
    private final PlatformType type;

    public Platform(Minecraft minecraft, PlatformType type) {
        this.minecraft = minecraft;
        this.type = type;
    }

    public PlatformType getPlatformType() {
        return this.type;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public abstract boolean isModLoaded(String id);

    public abstract @Nullable String getModVersion(String id);

    public abstract boolean isInModMenu();

    public enum PlatformType {
        FABRIC,
        NEOFORGE
    }
}
