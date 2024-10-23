package io.github.kabanfriends.craftgr.mixin;

import dev.isxander.yacl3.gui.OptionListWidget;
import io.github.kabanfriends.craftgr.config.controller.RadioStateController;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSelectionList.class)
public class MixinAbstractSelectionList {

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;mouseClicked(DDI)Z"))
    private boolean test(AbstractSelectionList.Entry<?> entry, double mouseX, double mouseY, int button) {
        if (entry instanceof OptionListWidget.OptionEntry optionEntry) {
            if (optionEntry.widget instanceof RadioStateController.Element radioElement) {
                return radioElement._mouseClicked();
            }
        }
        return entry.mouseClicked(mouseX, mouseY, button);
    }
}
