package io.github.kabanfriends.craftgr.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptionsScreen.class)
public class MixinGameOptionsScreen extends Screen {

    public MixinGameOptionsScreen(Text title) {
        super(title);
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    public void saveConfig(CallbackInfo ci) {
    }

}
