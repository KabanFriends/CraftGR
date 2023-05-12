package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.network.chat.Component;

public class RadioStateConfigEntry extends GRConfigEntry {

    public RadioStateConfigEntry(String key) {
        super(key, null);
    }

    @Override
    public Object deserialize(JsonPrimitive jsonValue) {
        return null;
    }

    @Override
    public JsonPrimitive serialize() {
        return null;
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Boolean.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .listener((option, value) -> {
                    AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
                    HandlerState state = handler.getState();

                    if (!value && state == HandlerState.ACTIVE) {
                        handler.stopPlayback();
                    } else if (value && (state == HandlerState.STOPPED || state == HandlerState.FAIL || state == HandlerState.NOT_INITIALIZED)) {
                        option.setAvailable(false);
                        CraftGR.EXECUTOR.submit(() -> {
                            handler.initialize();
                            option.setAvailable(true);
                            handler.startPlayback();
                        });
                    }
                })
                .binding(true, RadioStateConfigEntry::getToggleState, (value) -> {})
                .controller(TickBoxController::new)
                .instant(true)
                .build();
    }

    private static boolean getToggleState() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
        HandlerState state = handler.getState();

        return switch (state) {
            case READY, ACTIVE -> true;
            default -> false;
        };
    }
}
