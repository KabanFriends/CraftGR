package sh.kaban.craftgr.config.controller;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.audio.Radio;
import net.minecraft.ChatFormatting;
//? if > 1.21.1 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? }
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

            CraftGR.getInstance().getRadio().toggle();
            option.setAvailable(isButtonActive());
            playDownSound();
        }

        @Override
        protected Component getValueText() {
            option.setAvailable(isButtonActive());

            Radio radio = CraftGR.getInstance().getRadio();
            Radio.State state = radio.getState();

            if (radio.hasError()) {
                return Component.translatable("text.craftgr.config.option.playback.fail").withStyle(ChatFormatting.RED);
            }

            return switch (state) {
                case STOPPED -> Component.translatable("text.craftgr.config.option.playback.stopped");
                case AWAIT_LOADING, STARTING, CONNECTING -> Component.translatable("text.craftgr.config.option.playback.connecting");
                case PLAYING -> Component.translatable("text.craftgr.config.option.playback.playing");
            };
        }

        @Override
        //? if > 1.21.1 {
        public boolean keyPressed(KeyEvent event) {
            return _keyPressed(event.key());
        }
        //? } else {
        /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return _keyPressed(keyCode);
        }
        *///? }

        public boolean _keyPressed(int keyCode) {
            if (!isFocused()) {
                return false;
            }

            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
                toggleSetting();
                return true;
            }

            return false;
        }

        @Override
        //? if > 1.21.1 {
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return _mouseClicked(event.x(), event.y());
        }
        //? } else {
        /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return _mouseClicked(mouseX, mouseY);
        }
        *///? }

        public boolean _mouseClicked(double mouseX, double mouseY) {
            if (!isMouseOver(mouseX, mouseY) || !isAvailable()) {
                return false;
            }

            toggleSetting();
            return true;
        }

        private boolean isButtonActive() {
            Radio radio = CraftGR.getInstance().getRadio();
            Radio.State state = radio.getState();

            return switch (state) {
                case STOPPED, PLAYING -> true;
                case AWAIT_LOADING, STARTING, CONNECTING -> false;
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
