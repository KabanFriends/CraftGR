package sh.kaban.craftgr.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsSubScreen.class)
public class MixinOptionsSubScreen extends Screen {

    @Shadow
    protected OptionsList list;

    @Shadow
    @Final
    protected Options options;

    protected MixinOptionsSubScreen(Component title) {
        super(title);
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    protected void craftgr$saveConfig(CallbackInfo ci) {
    }
}
