package io.github.kabanfriends.craftgr.render.widget;

import com.mojang.blaze3d.vertex.PoseStack;

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

    public abstract void render(PoseStack poseStack, int mouseX, int mouseY);
}
