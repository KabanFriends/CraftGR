package io.github.kabanfriends.craftgr.fabric.mixin;

import dev.isxander.yacl3.gui.OptionListWidget;
import io.github.kabanfriends.craftgr.config.controller.RadioStateController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionListWidget.OptionEntry.class)
public class MixinOptionListWidgetOptionEntry {

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/gui/AbstractWidget;keyPressed(III)Z"), cancellable = true)
    private void craftgr$fixKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!((MixinAccessorAbstractSelectionListEntry<?>)this).getList().getFocused().equals(this)) {
            return;
        }
        if (((OptionListWidget.OptionEntry)(Object)this).widget instanceof RadioStateController.Element radioElement) {
            cir.setReturnValue(radioElement._keyPressed(keyCode));
            cir.cancel();
        }
    }
}
