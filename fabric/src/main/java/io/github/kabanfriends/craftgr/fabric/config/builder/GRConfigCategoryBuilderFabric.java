package io.github.kabanfriends.craftgr.fabric.config.builder;

import io.github.kabanfriends.craftgr.config.builder.GRConfigCategoryBuilder;
import io.github.kabanfriends.craftgr.fabric.config.value.*;

import java.awt.*;

public class GRConfigCategoryBuilderFabric extends GRConfigCategoryBuilder {

    @Override
    public GRConfigCategoryBuilder addString(String key, String defaultValue) {
        options.add(new StringConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addInteger(String key, int defaultValue) {
        options.add(new IntegerConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addFloat(String key, float defaultValue) {
        options.add(new FloatConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addPercentage(String key, int defaultValue) {
        options.add(new PercentageConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addBoolean(String key, boolean defaultValue) {
        options.add(new BooleanConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addEnum(String key, Enum defaultValue) {
        options.add(new EnumConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addColor(String key, Color defaultValue) {
        options.add(new ColorConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addOverlayWidth(String key, int defaultValue) {
        options.add(new OverlayWidthConfigOptionFabric(key, defaultValue));
        return this;
    }

    @Override
    public GRConfigCategoryBuilder addRadioState(String key) {
        options.add(new RadioStateConfigOptionFabric(key));
        return this;
    }
}
