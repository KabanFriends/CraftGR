package io.github.kabanfriends.craftgr.config.compat;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.compat.impl.Cloth7Compat;
import io.github.kabanfriends.craftgr.config.compat.impl.Cloth8Compat;
import io.github.kabanfriends.craftgr.platform.Platform;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class ClothCompat {

    public abstract void setDefaultValue(FieldBuilder builder, Object value);

    public abstract void setTooltip(FieldBuilder builder, Component tooltip);

    public abstract void setSaveConsumer(FieldBuilder builder, Consumer consumer);

    private static ClothCompat compat;

    public static ClothCompat getCompat() {
        return compat;
    }

    public static void init() {
        String modId = "cloth-config";
        if (CraftGR.getPlatform().getPlatformType() == Platform.PlatformType.FORGE) {
            modId = "cloth_config";
        }

        if (CraftGR.getPlatform().isModLoaded(modId)) {
            compat = getCompatForVersion(CraftGR.getPlatform().getModVersion(modId));
        }
    }

    private static ClothCompat getCompatForVersion(String version) {
        if (version.matches("^7\\..*")) {
            return new Cloth7Compat();
        }
        return new Cloth8Compat();
    }
}
