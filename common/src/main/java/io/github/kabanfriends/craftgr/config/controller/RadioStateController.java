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
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.RadioStream;
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
            if (!isAvailable()) {
                return;
            }

            CraftGR.getInstance().getRadioStream().toggle();
            option.setAvailable(isButtonActive());
            playDownSound();
        }

        @Override
        protected Component getValueText() {
            option.setAvailable(isButtonActive());

            RadioStream stream = CraftGR.getInstance().getRadioStream();
            RadioStream.State state = stream.getState();

            if (stream.hasError()) {
                return Component.translatable("text.craftgr.config.option.playback.fail").withStyle(ChatFormatting.RED);
            }

            return switch (state) {
                case STOPPED -> Component.translatable("text.craftgr.config.option.playback.stopped");
                case AWAIT_LOADING, CONNECTING -> Component.translatable("text.craftgr.config.option.playback.connecting");
                case PLAYING -> Component.translatable("text.craftgr.config.option.playback.playing");
            };
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isFocused()) {
                return false;
            }

            return _keyPressed(keyCode);
        }

        public boolean _keyPressed(int keyCode) {
            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
                toggleSetting();
                return true;
            }

            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY)) {
                return false;
            }

            return _mouseClicked();
        }

        public boolean _mouseClicked() {
            if (!isAvailable()) {
                return false;
            }

            toggleSetting();
            return true;
        }

        private boolean isButtonActive() {
            RadioStream stream = CraftGR.getInstance().getRadioStream();
            RadioStream.State state = stream.getState();

            return switch (state) {
                case STOPPED, PLAYING -> true;
                case CONNECTING, AWAIT_LOADING -> false;
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
