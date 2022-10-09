package io.github.kabanfriends.craftgr.mixin;

import com.mojang.serialization.Codec;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen {

    private static final ResourceLocation CONFIG_BUTTON = new ResourceLocation(CraftGR.MOD_ID, "textures/button_config.png");

    private static final OptionInstance<Double> PLAYBACK_VOLUME = new OptionInstance<>(
            "text.craftgr.gui.options.volume",
            OptionInstance.noTooltip(),
            (component, value) -> {
                double doubleValue = (double) value;
                return doubleValue == 0.0D ? Component.translatable("options.generic_value", component, CommonComponents.OPTION_OFF) : Component.translatable("options.percent_value", component, (int)(doubleValue * 100.0D));
            },
            OptionInstance.UnitDouble.INSTANCE,
            Codec.doubleRange(0.0D, 1.0D),
            0.0D,
            (value) -> {
                GRConfig.setValue("volume", (int)((double)value * 100.0D));
                if (AudioPlayerHandler.getInstance().isPlaying()) {
                    AudioPlayerHandler.getInstance().getAudioPlayer().setVolume(1.0f);
                }
            });

    public MixinSoundOptionsScreen(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        PLAYBACK_VOLUME.set(GRConfig.<Integer>getValue("volume") / 100.0D);

        this.addRenderableWidget(PLAYBACK_VOLUME.createButton(CraftGR.MC.options, this.width / 2 - 155 + 160, this.height / 6 - 12 + 22 * (11 >> 1), 150 - 24));
        this.addRenderableWidget(new ImageButton(this.width / 2 - 155 + 160 + 150 - 20, this.height / 6 - 12 + 22 * (11 >> 1), 20, 20, 0, 0, 20, CONFIG_BUTTON, 20, 40, (button) -> {
            CraftGR.getPlatform().openConfigScreen();
        }));
    }

    @Override
    public void saveConfig(CallbackInfo ci) {
        GRConfig.save();
    }
}
