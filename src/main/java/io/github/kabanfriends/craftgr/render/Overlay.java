package io.github.kabanfriends.craftgr.render;

import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class Overlay {

    abstract public void render(MatrixStack matrix, int mouseX, int mouseY);

    abstract public void onMouseClick(int mouseX, int mouseY, CallbackInfo info);

}
