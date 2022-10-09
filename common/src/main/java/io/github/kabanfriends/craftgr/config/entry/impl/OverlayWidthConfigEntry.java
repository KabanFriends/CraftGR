package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class OverlayWidthConfigEntry extends GRConfigEntry<Integer> {

    private static final int MIN_VALUE = 35;
    private static final int MAX_VALUE = 435;

    private static final int WIDTH_OFFSET = SongInfoOverlay.ART_LEFT_PADDING
            + SongInfoOverlay.ART_SIZE
            + SongInfoOverlay.ART_INFO_SPACE_WIDTH
            + SongInfoOverlay.INFO_RIGHT_PADDING;

    public OverlayWidthConfigEntry(String key, int value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        return Mth.clamp(jsonValue.getAsInt(), MIN_VALUE, MAX_VALUE);
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        SongInfoOverlay.getInstance().setScrollWidth((int)value);
    }

    public AbstractFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        return builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), MIN_VALUE, MAX_VALUE)
                .setDefaultValue(getDefaultValue())
                .setTextGetter(value -> Component.literal((WIDTH_OFFSET + value * 2) + "px"));
    }
}
