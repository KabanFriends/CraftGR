package io.github.kabanfriends.craftgr.render;

import net.minecraft.client.util.math.MatrixStack;

public abstract class Overlay {

    abstract public void render(MatrixStack matrix, int mouseX, int mouseY);

}
