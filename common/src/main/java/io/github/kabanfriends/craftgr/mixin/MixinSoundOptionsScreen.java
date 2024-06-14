package io.github.kabanfriends.craftgr.mixin;

import com.mojang.serialization.Codec;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.gui.RadioOptionContainer;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.ThreadLocals;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen {

    private AbstractWidget volumeSlider;
    private AbstractWidget configButton;

    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "config"),
            ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "config_highlighted")
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

    @Inject(method = "addOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V", shift = At.Shift.BEFORE, ordinal = 0))
    private void craftgr$startSmallOptions(CallbackInfo ci) {
        ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.set(false);
    }

    @Inject(method = "addOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void craftgr$endSmallOptions(CallbackInfo ci) {
        Boolean added = ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.get();
        if (added != null && !added) {
            ((MixinAccessorAbstractSelectionList) this.list).craftgr$addEntry(OptionsList.Entry.small(new RadioOptionContainer(0, 0, 150), null, this));
        }
        ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.remove();
    }

    @Override
    public void craftgr$saveConfig(CallbackInfo ci) {
        GRConfig.save();
    }
}