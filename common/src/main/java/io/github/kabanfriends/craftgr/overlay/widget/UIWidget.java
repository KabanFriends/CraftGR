package io.github.kabanfriends.craftgr.overlay.widget;

import net.minecraft.client.gui.GuiGraphics;

public abstract class UIWidget {

    protected float x;
    protected float y;

    public UIWidget(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);
}
