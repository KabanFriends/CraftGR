package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.RadioStateConfigOption;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.network.chat.Component;

public class RadioStateConfigOptionFabric extends RadioStateConfigOption implements FabricConfigBuildable {

    public RadioStateConfigOptionFabric(String key) {
        super(key);
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
                .binding(false, RadioStateConfigOptionFabric::getToggleState, (value) -> {})
                .controller(TickBoxController::new)
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
