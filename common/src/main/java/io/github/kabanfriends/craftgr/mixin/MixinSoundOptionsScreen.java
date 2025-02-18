package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.gui.RadioOptionContainer;
import io.github.kabanfriends.craftgr.util.ThreadLocals;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen {

    private MixinSoundOptionsScreen(Component title) {
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
    protected void craftgr$saveConfig(CallbackInfo ci) {
        CraftGR.getInstance().getConfig().save();
    }
}