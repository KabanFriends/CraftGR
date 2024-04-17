package io.github.kabanfriends.craftgr.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface MixinAccessorAbstractSelectionList {

    @Invoker("addEntry")
    int addEntry(AbstractSelectionList.Entry entry);
}
