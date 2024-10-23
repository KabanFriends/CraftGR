package io.github.kabanfriends.craftgr.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(AbstractSelectionList.Entry.class)
public interface MixinAccessorAbstractSelectionListEntry<E extends AbstractSelectionList.Entry<E>> {

    @Accessor
    AbstractSelectionList<E> getList();
}
