package io.github.kabanfriends.craftgr.overlay.widget.impl;

import io.github.kabanfriends.craftgr.overlay.widget.UIWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class ScrollingText extends UIWidget {

    private int width;
    private float stepSize;
    private int waitTime;

    private float startPos = 0;
    private float moveDifference = 0;
    private long lastTime = 0;
    private State state = State.WAITING;

    private Component component;

    public ScrollingText(float x, float y, Component component) {
        super(x, y);

        this.component = component;

        this.width = 100;
        this.stepSize = 1;
        this.waitTime = 4000;
    }

    public Component getText() {
        return component;
    }

    public void setText(Component component) {
        this.component = component;
    }

    public void resetScroll() {
        state = State.WAITING;
        moveDifference = 0;
        lastTime = 0;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getStepSize() {
        return stepSize;
    }

    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waittime) {
        waitTime = waittime;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int fontX = (int) getMovingX(x / 2);
        int fontY = (int) y / 2;

        int scissorX = (int) x / 2;

        Matrix3x2fStack matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.scale(2, 2);
        graphics.enableScissor(scissorX - 1, fontY - 1, scissorX + width + 2, fontY + Minecraft.getInstance().font.lineHeight + 2);
        //graphics.fill(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), 0x8F00FF00);

        graphics.drawString(Minecraft.getInstance().font, component, fontX, fontY, Color.WHITE.getRGB());
        graphics.disableScissor();
        matrixStack.popMatrix();}

    private float getMovingX(float x) {
        final float textWidth = Minecraft.getInstance().font.width(component);

        if (width < textWidth) {
            final float maxMove = width - textWidth;

            if (lastTime == 0) {
                lastTime = Util.getMillis();
            }

            if (state == State.WAITING) {
                if (hasWaitTimePassed()) {
                    startPos = moveDifference;
                    lastTime = 0;
                    state = moveDifference >= 0 ? State.LEFT : State.RIGHT;
                }
            } else {
                moveDifference = startPos + (Util.getMillis() - lastTime) * (state == State.LEFT ? -stepSize : stepSize) / 32f;
                if (state == State.LEFT ? moveDifference <= maxMove : moveDifference >= 0) {
                    moveDifference = state == State.LEFT ? maxMove : 0;
                    lastTime = 0;
                    state = State.WAITING;
                }
            }

            return x + moveDifference;
        }
        return x;
    }

    protected boolean hasWaitTimePassed() {
        return Util.getMillis() - waitTime >= lastTime;
    }

    private enum State {
        WAITING,
        LEFT,
        RIGHT
    }
}
