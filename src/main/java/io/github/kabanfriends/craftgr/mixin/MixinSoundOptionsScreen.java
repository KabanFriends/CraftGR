package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinGameOptionsScreen {

    private static final Identifier CONFIG_BUTTON = new Identifier(CraftGR.MOD_ID, "textures/button_config.png");

    private static final DoubleOption PLAYBACK_VOLUME = new DoubleOption("options.craftgr.playback_volume", 0, 100, 1, (gameOptions) -> {
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
        this.addButton(new DoubleOptionSliderWidget(CraftGR.MC.options, this.width / 2 - 155 + 160, this.height / 6 - 12 + 24 * (11 >> 1), 150 - 24, 20, PLAYBACK_VOLUME));
        this.addButton(new TexturedButtonWidget(this.width / 2 - 155 + 160 + 150 - 20, this.height / 6 - 12 + 24 * (11 >> 1), 20, 20, 0, 0, 20, CONFIG_BUTTON, 20, 40, (button) -> {
            CraftGR.MC.openScreen(AutoConfig.getConfigScreen(GRConfig.class, CraftGR.MC.currentScreen).get());
        }));
    }

    @Override
    public void saveConfig(CallbackInfo ci) {
        AutoConfig.getConfigHolder(GRConfig.class).save();
    }
}
