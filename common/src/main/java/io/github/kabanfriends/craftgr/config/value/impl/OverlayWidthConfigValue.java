package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class OverlayWidthConfigValue extends GRConfigValue<Integer> {

    private static final int MIN_VALUE = 35;
    private static final int MAX_VALUE = 435;

    private static final int WIDTH_OFFSET = SongInfoOverlay.ART_LEFT_PADDING
            + SongInfoOverlay.ART_SIZE
            + SongInfoOverlay.ART_INFO_SPACE_WIDTH
            + SongInfoOverlay.INFO_RIGHT_PADDING;

    public OverlayWidthConfigValue(String key, int value) {
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
        if (SongInfoOverlay.getInstance() != null) {
            SongInfoOverlay.getInstance().updateScrollWidth();
        }
    }

    public IntSliderBuilder getBuilder(ConfigEntryBuilder builder) {
        IntSliderBuilder field = builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), MIN_VALUE, MAX_VALUE)
                .setTextGetter(value -> Component.literal((WIDTH_OFFSET + value * 2) + "px"));
        field.setDefaultValue(getDefaultValue());
        return field;
    }
}
