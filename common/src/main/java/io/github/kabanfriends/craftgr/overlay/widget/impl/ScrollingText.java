package io.github.kabanfriends.craftgr.overlay.widget.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.overlay.widget.UIWidget;
import io.github.kabanfriends.craftgr.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

//Code based on: https://github.com/MC-U-Team/U-Team-Core/blob/1.19.2/src/main/java/info/u_team/u_team_core/gui/elements/ScrollingText.java
public class ScrollingText extends UIWidget {

    protected int width;
    protected float stepSize;
    protected int waitTime;

    protected float startPos = 0;
    protected float moveDifference = 0;
    protected long lastTime = 0;
    protected State state = State.WAITING;

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
        float scale = ModConfig.<Float>get("overlayScale") * 2;

        int fontX = (int)getMovingX(x / 2f);
        int fontY = (int)y / 2;

        int scissorX = (int)(scale * (int)(x / 2f));
        int scissorY = (int)(scale * fontY);

        int scissorW = (int)(width * scale);
        int scissorH = (int)(CraftGR.getInstance().getMinecraft().font.lineHeight * scale);

        PoseStack poseStack = graphics.pose();

        RenderUtil.enableUnscaledScissor(graphics, scissorX, scissorY, scissorX + scissorW, scissorY + scissorH);

        // Uncomment to debug
        //RenderUtil.fill(poseStack, 0, 0, CraftGR.getInstance().getMinecraft().getWindow().getWidth(), CraftGR.getInstance().getMinecraft().getWindow().getHeight(), 0x8F00FF00, 0.6f);

        poseStack.pushPose();
        poseStack.scale(2, 2, 2);
        graphics.drawString(CraftGR.getInstance().getMinecraft().font, component, fontX, fontY, Color.WHITE.getRGB());
        poseStack.popPose();

        graphics.disableScissor();
    }

    private float getMovingX(float x) {
        final float textWidth = CraftGR.getInstance().getMinecraft().font.width(component);

        if (width < textWidth) {
            final float maxMove = width - textWidth;

            if (lastTime == 0) {
                lastTime = System.currentTimeMillis();
            }

            if (state == State.WAITING) {
                if (hasWaitTimePassed()) {
                    startPos = moveDifference;
                    lastTime = 0;
                    state = moveDifference >= 0 ? State.LEFT : State.RIGHT;
                }
            } else {
                moveDifference = startPos + (System.currentTimeMillis() - lastTime) * (state == State.LEFT ? -stepSize : stepSize) / 32f;
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
        return System.currentTimeMillis() - waitTime >= lastTime;
    }

    private enum State {
        WAITING,
        LEFT,
        RIGHT
    }
}
