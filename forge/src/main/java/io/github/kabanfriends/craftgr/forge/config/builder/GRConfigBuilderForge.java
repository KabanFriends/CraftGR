package io.github.kabanfriends.craftgr.forge.config.builder;

import io.github.kabanfriends.craftgr.config.builder.GRConfigBuilder;
import io.github.kabanfriends.craftgr.config.builder.GRConfigCategoryBuilder;

public class GRConfigBuilderForge extends GRConfigBuilder {

    @Override
    public GRConfigCategoryBuilder getCategoryBuilder() {
        return new GRConfigCategoryBuilderForge();
    }
}
