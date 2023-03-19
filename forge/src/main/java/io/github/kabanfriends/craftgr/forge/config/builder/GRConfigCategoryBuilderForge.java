package io.github.kabanfriends.craftgr.forge.config.builder;

import io.github.kabanfriends.craftgr.config.builder.GRConfigCategoryBuilder;
import io.github.kabanfriends.craftgr.forge.config.value.*;

import java.awt.*;

public class GRConfigCategoryBuilderForge extends GRConfigCategoryBuilder {

    @Override
    public GRConfigCategoryBuilder addString(String key, String defaultValue) {
        options.add(new StringConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addInteger(String key, int defaultValue) {
        options.add(new IntegerConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addFloat(String key, float defaultValue) {
        options.add(new FloatConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addPercentage(String key, int defaultValue) {
        options.add(new PercentageConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addBoolean(String key, boolean defaultValue) {
        options.add(new BooleanConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addEnum(String key, Enum defaultValue) {
        options.add(new EnumConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addColor(String key, Color defaultValue) {
        options.add(new ColorConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addOverlayWidth(String key, int defaultValue) {
        options.add(new OverlayWidthConfigOptionForge(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addRadioState(String key) {
        options.add(new RadioStateConfigOptionForge(key));
        return this;
    }
}
