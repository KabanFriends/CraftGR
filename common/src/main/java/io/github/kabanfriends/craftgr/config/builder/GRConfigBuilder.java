package io.github.kabanfriends.craftgr.config.builder;

import io.github.kabanfriends.craftgr.config.GRConfigCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class GRConfigBuilder {

    public List<GRConfigCategory> categories = new ArrayList<>();

    public GRConfigCategory[] build() {
        return categories.toArray(new GRConfigCategory[0]);
    }

    public GRConfigBuilder addCategory(GRConfigCategory category) {
        categories.add(category);
        return this;
    }

    public abstract GRConfigCategoryBuilder getCategoryBuilder();
}
