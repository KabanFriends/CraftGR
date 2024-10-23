package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.gui.RadioOptionContainer;
import io.github.kabanfriends.craftgr.util.ThreadLocals;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(OptionsList.OptionEntry.class)
public class MixinOptionsListOptionEntry {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList$Entry;<init>(Ljava/util/List;Lnet/minecraft/client/gui/screens/Screen;)V"))
    private static List<AbstractWidget> craftgr$initOptionEntry(List<AbstractWidget> list) {
        Boolean value = ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.get();
        if (value == null || list.size() > 1 || value) {
            return list;
        }

        ThreadLocals.RADIO_OPTION_CONTAINER_ADDED.set(true);
        return List.of(list.get(0), new RadioOptionContainer(0, 0, 150));
    }
}
