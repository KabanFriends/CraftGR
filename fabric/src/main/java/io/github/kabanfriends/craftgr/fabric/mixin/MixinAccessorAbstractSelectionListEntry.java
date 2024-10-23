package io.github.kabanfriends.craftgr.fabric.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.Entry.class)
public interface MixinAccessorAbstractSelectionListEntry<E extends AbstractSelectionList.Entry<E>> {

    @Accessor
    AbstractSelectionList<E> getList();
}
