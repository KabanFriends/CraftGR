package io.github.kabanfriends.craftgr.fabric.config.builder;

import io.github.kabanfriends.craftgr.config.builder.GRConfigBuilder;
import io.github.kabanfriends.craftgr.config.builder.GRConfigCategoryBuilder;

public class GRConfigBuilderFabric extends GRConfigBuilder {

    @Override
    public GRConfigCategoryBuilder getCategoryBuilder() {
        return new GRConfigCategoryBuilderFabric();
    }
}
