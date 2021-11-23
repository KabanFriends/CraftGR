package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinGameOptionsScreen {

    private static final DoubleOption PLAYBACK_VOLUME = new DoubleOption("none", 0, 100, 1, (gameOptions) -> {
        return (double) GRConfig.getConfig().volume;
    }, (gameOptions, volume) -> {
        GRConfig.getConfig().volume = volume.intValue();
        AudioPlayerHandler.getInstance().player.setVolume(1.0f);
    }, (gameOptions, option) -> {
        return new TranslatableText("options.percent_value", new TranslatableText("text.craftgr.gui.options.volume"), GRConfig.getConfig().volume);
    });

    public MixinSoundOptionsScreen(LiteralText literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addButton(new DoubleOptionSliderWidget(CraftGR.MC.options, this.width / 2 - 155 + 160, this.height / 6 - 12 + 24 * (11 >> 1), 150, 20, PLAYBACK_VOLUME));
    }

    @Override
    public void saveConfig(CallbackInfo ci) {
        AutoConfig.getConfigHolder(GRConfig.class).save();
    }
}
