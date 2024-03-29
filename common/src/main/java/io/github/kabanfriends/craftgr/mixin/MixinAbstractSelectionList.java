package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.mixinaccess.SoundOptionsScreenMixinAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public abstract class MixinAbstractSelectionList {

    @Shadow public abstract double getScrollAmount();

    @Shadow @Final protected int itemHeight;

    @Shadow protected int headerHeight;

    @Inject(method = "renderList", at = @At("HEAD"))
    private void craftgr$renderSelectionList(GuiGraphics graphics, int i, int j, float f, CallbackInfo ci) {
        if (CraftGR.MC.screen instanceof SoundOptionsScreen screen) {
            AbstractSelectionList instance = (AbstractSelectionList)(Object)this;
            SoundOptionsScreenMixinAccess access = (SoundOptionsScreenMixinAccess)screen;

            if (instance == access.getOptionsList()) {
                AbstractWidget volumeSlider = access.getVolumeSlider();
                AbstractWidget configButton = access.getConfigButton();

                //AbstractSelectionList#getRowTop
                int y = instance.getY() + 4 - (int)getScrollAmount() + 5 * itemHeight + headerHeight;
                volumeSlider.setY(y);
                configButton.setY(y);

                volumeSlider.render(graphics, i, j, f);
                configButton.render(graphics, i, j, f);
            }
        }
    }

}
