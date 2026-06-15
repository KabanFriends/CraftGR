package io.github.kabanfriends.craftgr.gui;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.Radio;
import io.github.kabanfriends.craftgr.config.ModConfig;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RadioVolumeSliderButton extends AbstractSliderButton {

    private static final Component SLIDER_NAME = Component.translatable("text.craftgr.button.volume");

    public RadioVolumeSliderButton(int x, int y, int width) {
        super(x, y, width, 20, CommonComponents.EMPTY, ModConfig.<Integer>get("volume") / 100.0);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int volume = ModConfig.get("volume");
        this.setMessage(percentValueOrOffLabel(SLIDER_NAME, volume));
    }

    @Override
    protected void applyValue() {
        CraftGR.getInstance().getRadio().setVolume(value);

        int volume = (int) Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), 0, 100);
        ModConfig.set("volume", volume);
    }

    private static Component percentValueOrOffLabel(Component caption, int value) {
        return value == 0.0 ?
                Options.genericValueLabel(caption, CommonComponents.OPTION_OFF) :
                Component.translatable("options.percent_value", caption, value);
    }
}
