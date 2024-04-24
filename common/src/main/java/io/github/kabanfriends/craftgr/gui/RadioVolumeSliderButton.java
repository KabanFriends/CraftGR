package io.github.kabanfriends.craftgr.gui;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RadioVolumeSliderButton extends AbstractSliderButton {

    public RadioVolumeSliderButton(int x, int y, int width) {
        super(x, y, width, 20, CommonComponents.EMPTY, GRConfig.<Integer>getValue("volume") / 100d);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        Component name = Component.translatable("text.craftgr.gui.options.volume");
        int volume = GRConfig.getValue("volume");
        this.setMessage(volume == 0 ? Component.translatable("options.generic_value", name, CommonComponents.OPTION_OFF) : Component.translatable("options.percent_value", name, volume));
    }

    @Override
    protected void applyValue() {
        int volume = (int) Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), 0, 100);
        GRConfig.setValue("volume", volume);
        if (AudioPlayerHandler.getInstance().isPlaying()) {
            AudioPlayerHandler.getInstance().getAudioPlayer().setBaseVolume(1.0f);
        }
    }
}
