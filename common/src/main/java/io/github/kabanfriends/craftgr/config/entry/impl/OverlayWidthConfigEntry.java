package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
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
        if (SongInfoOverlay.getInstance() != null) {
            SongInfoOverlay.getInstance().updateScrollWidth();
        }
    }

    public Option<Integer> getOption() {
        return Option.<Integer>createBuilder()
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                .controller((option) -> IntegerSliderControllerBuilder.create(option)
                        .step(1)
                        .range(MIN_VALUE, MAX_VALUE)
                        .formatValue((value) -> Component.literal((WIDTH_OFFSET + value * 2) + "px"))
                )
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
