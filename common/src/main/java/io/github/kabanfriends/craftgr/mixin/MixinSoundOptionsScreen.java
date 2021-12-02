package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen {

    private static final ResourceLocation CONFIG_BUTTON = new ResourceLocation(CraftGR.MOD_ID, "textures/button_config.png");

    private static final ProgressOption PLAYBACK_VOLUME = new ProgressOption("options.craftgr.playback_volume", 0, 100, 1, (gameOptions) -> {
        return (double) GRConfig.getConfig().volume;
    }, (gameOptions, volume) -> {
        GRConfig.getConfig().volume = volume.intValue();
        AudioPlayerHandler.getInstance().player.setVolume(1.0f);
    }, (gameOptions, option) -> {
        return new TranslatableComponent("options.percent_value", new TranslatableComponent("text.craftgr.gui.options.volume"), GRConfig.getConfig().volume);
    });

    public MixinSoundOptionsScreen(TextComponent title) { super(title); }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addRenderableWidget(new SliderButton(CraftGR.MC.options, this.width / 2 - 155 + 160, this.height / 6 - 12 + 22 * (11 >> 1), 150 - 24, 20, PLAYBACK_VOLUME, null));
        this.addRenderableWidget(new ImageButton(this.width / 2 - 155 + 160 + 150 - 20, this.height / 6 - 12 + 22 * (11 >> 1), 20, 20, 0, 0, 20, CONFIG_BUTTON, 20, 40, (button) -> {
            CraftGR.MC.setScreen(AutoConfig.getConfigScreen(GRConfig.class, CraftGR.MC.screen).get());
        }));
    }

    @Override
    public void saveConfig(CallbackInfo ci) {
        AutoConfig.getConfigHolder(GRConfig.class).save();
    }
}
