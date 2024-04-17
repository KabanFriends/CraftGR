package io.github.kabanfriends.craftgr.mixin;

import com.mojang.serialization.Codec;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.gui.RadioConfigContainer;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.mixinaccess.SoundOptionsScreenMixinAccess;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen implements SoundOptionsScreenMixinAccess {

    @Shadow
    private OptionsList list;

    private AbstractWidget volumeSlider;
    private AbstractWidget configButton;

    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation(CraftGR.MOD_ID, "config"),
            new ResourceLocation(CraftGR.MOD_ID, "config_highlighted")
    );

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
                    AudioPlayerHandler.getInstance().getAudioPlayer().setBaseVolume(1.0f);
                }
            });

    public MixinSoundOptionsScreen(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void craftgr$initScreen(CallbackInfo ci) {
        ((MixinAccessorAbstractSelectionList) this.list).addEntry(OptionsList.Entry.big(List.of(new RadioConfigContainer(0, 0, 150)), this));
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V", ordinal = 0))
    private void craftgr$addSmallOptions(OptionsList instance, OptionInstance<?>[] optionInstances) {
        for(int i = 0; i < optionInstances.length; i += 2) {
            OptionInstance<?> optionInstance = i < optionInstances.length - 1 ? optionInstances[i + 1] : null;
            this.addEntry(OptionsList.OptionEntry.small(this.minecraft.options, optionInstances[i], optionInstance, this.screen));
        }
    }

    @Override
    public void craftgr$saveConfig(CallbackInfo ci) {
        GRConfig.save();
    }

    @Override
    public OptionsList getOptionsList() {
        return list;
    }

    @Override
    public AbstractWidget getVolumeSlider() {
        return volumeSlider;
    }

    @Override
    public AbstractWidget getConfigButton() {
        return configButton;
    }
}