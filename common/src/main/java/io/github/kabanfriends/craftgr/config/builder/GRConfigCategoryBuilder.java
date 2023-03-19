package io.github.kabanfriends.craftgr.config.builder;

import io.github.kabanfriends.craftgr.config.GRConfigCategory;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GRConfigCategoryBuilder {

    public List<GRConfigOption<?>> options = new ArrayList<>();
    public Component title;
    public boolean expanded;

    public GRConfigCategory build() {
        return new GRConfigCategory(title, expanded, options.toArray(new GRConfigOption[0]));
    }

    public GRConfigCategoryBuilder setTitle(Component title) {
        this.title = title;
        return this;
    }

    public GRConfigCategoryBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    public abstract GRConfigCategoryBuilder addString(String key, String defaultValue);

    public abstract GRConfigCategoryBuilder addInteger(String key, int defaultValue);

    public abstract GRConfigCategoryBuilder addFloat(String key, float defaultValue);

    public abstract GRConfigCategoryBuilder addPercentage(String key, int defaultValue);

    public abstract GRConfigCategoryBuilder addBoolean(String key, boolean defaultValue);

    public abstract GRConfigCategoryBuilder addEnum(String key, Enum defaultValue);

    public abstract GRConfigCategoryBuilder addColor(String key, Color defaultValue);

    public abstract GRConfigCategoryBuilder addOverlayWidth(String key, int defaultValue);

    public abstract GRConfigCategoryBuilder addRadioState(String key);
}
