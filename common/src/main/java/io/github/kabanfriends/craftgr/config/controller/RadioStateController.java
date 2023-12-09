package io.github.kabanfriends.craftgr.config.controller;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class RadioStateController implements Controller<Boolean> {

    private final Option<Boolean> option;

    public RadioStateController(Option<Boolean> option) {
        this.option = option;
    }

    @Override
    public Option<Boolean> option() {
        return option;
    }

    @Override
    public Component formatValue() {
        return Component.empty();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new Element(this, option, screen, widgetDimension);
    }

    public static class Element extends ControllerWidget<RadioStateController> {

        private final Option<Boolean> option;

        public Element(RadioStateController control, Option<Boolean> option, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
            this.option = option;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        public void toggleSetting() {
            if (!isAvailable()) return;

            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
            HandlerState state = handler.getState();

            if (state == HandlerState.ACTIVE) {
                handler.stopPlayback();
            } else if (state != HandlerState.INITIALIZING) {
                AudioPlayerUtil.startPlaybackAsync();
            }

            option.setAvailable(isButtonActive());
            playDownSound();
        }

        @Override
        protected Component getValueText() {
            option.setAvailable(isButtonActive());

            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
            HandlerState state = handler.getState();

            return switch (state) {
                case NOT_INITIALIZED, STOPPED -> Component.translatable("text.craftgr.config.option.playback.stopped");
                case RELOADING, INITIALIZING -> Component.translatable("text.craftgr.config.option.playback.connecting");
                case READY, ACTIVE -> Component.translatable("text.craftgr.config.option.playback.playing");
                case FAIL -> Component.translatable("text.craftgr.config.option.playback.fail").withStyle(ChatFormatting.RED);
            };
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isFocused()) return false;

            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
                toggleSetting();
                return true;
            }

            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || !isAvailable()) return false;

            toggleSetting();
            return true;
        }

        private boolean isButtonActive() {
            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
            HandlerState state = handler.getState();

            return switch (state) {
                case NOT_INITIALIZED, STOPPED, READY, ACTIVE, FAIL -> true;
                case RELOADING, INITIALIZING -> false;
            };
        }
    }

    public static class Builder extends AbstractControllerBuilderImpl<Boolean> implements ControllerBuilder<Boolean> {
        public Builder(Option<Boolean> option) {
            super(option);
        }

        @Override
        public Controller<Boolean> build() {
            return new RadioStateController(option);
        }
    }
}
