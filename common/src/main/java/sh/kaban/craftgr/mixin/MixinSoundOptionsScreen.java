package sh.kaban.craftgr.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.rendering.gui.RadioOptionContainer;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends MixinOptionsSubScreen {

    private MixinSoundOptionsScreen(Component title) {
        super(title);
    }

    @Redirect(method = "addOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V", ordinal = 0))
    private void craftgr$addRadioContainer(OptionsList instance, OptionInstance<?>[] options) {
        RadioOptionContainer widget = new RadioOptionContainer(0, 0, 150);

        if (options.length % 2 == 0) {
            // Radio container is on its own row
            list.addSmall(options);
            list.addSmall(widget, null);
        } else {
            // Radio container is on the same row as the last option
            // (add the last option with the container as the second)
            OptionInstance<?> last = options[options.length - 1];
            list.addSmall(Arrays.copyOf(options, options.length - 1));
            //? if > 1.21.1 {
            list.addSmall(last.createButton(this.options), last, widget);
            //? } else
            //list.addSmall(last.createButton(this.options), widget);
        }
    }

    @Override
    protected void craftgr$saveConfig(CallbackInfo ci) {
        CraftGR.getInstance().getConfig().save();
    }
}