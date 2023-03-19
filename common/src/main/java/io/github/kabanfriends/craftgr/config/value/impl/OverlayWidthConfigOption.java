package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.util.Mth;

public abstract class OverlayWidthConfigOption extends GRConfigOption<Integer> {

    protected static final int MIN_VALUE = 35;
    protected static final int MAX_VALUE = 435;

    protected static final int WIDTH_OFFSET = SongInfoOverlay.ART_LEFT_PADDING
            + SongInfoOverlay.ART_SIZE
            + SongInfoOverlay.ART_INFO_SPACE_WIDTH
            + SongInfoOverlay.INFO_RIGHT_PADDING;

    public OverlayWidthConfigOption(String key, int value) {
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
}
