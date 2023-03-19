package io.github.kabanfriends.craftgr.config.value.impl;

import io.github.kabanfriends.craftgr.config.value.GRConfigOption;

import java.awt.*;

public abstract class ColorConfigOption extends GRConfigOption<Color> {

    public ColorConfigOption(String key, Color value) {
        super(key, value);
    }
}
