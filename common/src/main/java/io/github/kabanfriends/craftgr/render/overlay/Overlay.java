package io.github.kabanfriends.craftgr.render.overlay;


import net.minecraft.client.gui.GuiGraphics;

public abstract class Overlay {

    abstract public void render(GuiGraphics graphics, int mouseX, int mouseY);

    abstract public boolean onMouseClick(int mouseX, int mouseY);

}
