package io.github.kabanfriends.craftgr.mixin;

import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsSubScreen.class)
public class MixinGameOptionsScreen extends Screen {

    public MixinGameOptionsScreen(TextComponent title) {
        super(title);
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    public void saveConfig(CallbackInfo ci) {
    }

}
