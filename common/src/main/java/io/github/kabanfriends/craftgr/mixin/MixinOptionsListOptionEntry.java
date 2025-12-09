package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.util.ThreadLocals;
import net.minecraft.client.gui.components.OptionsList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(OptionsList.Entry.class)
public class MixinOptionsListOptionEntry {

    @Shadow @Final @Mutable
    List<OptionsList.OptionInstanceWidget> children;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/OptionsList$Entry;children:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
    private void craftgr$addRadioToChildren(OptionsList.Entry instance, List<OptionsList.OptionInstanceWidget> value) {
        Boolean added = ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.get();
        if (added == null || value.size() > 1 || added) {
            this.children = value;
            return;
        }

        ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.set(true);
        this.children = List.copyOf(value);
    }
}
