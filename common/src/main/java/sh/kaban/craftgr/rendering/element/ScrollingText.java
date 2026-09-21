package sh.kaban.craftgr.rendering.element;

import sh.kaban.craftgr.config.ModConfig;
import net.minecraft.client.Minecraft;
//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
//? if > 1.21.1 {
import net.minecraft.util.Util;
//? } else
//import net.minecraft.Util;

import java.awt.*;

public class ScrollingText extends RenderableElement {

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
    //? if > 1.21.11 {
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    //? } else
    //public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int fontX = (int) getMovingX(x / 2);
        int fontY = (int) y / 2;

        int scissorX = (int) x / 2;

        var pose = graphics.pose();
        //? if > 1.21.1 {
        pose.pushMatrix();
        pose.scale(2, 2);
        //? } else {
        /*pose.pushPose();
        pose.scale(2f, 2f, 2f);
        *///? }

        //? if > 1.21.1 {
        graphics.enableScissor(
        //? } else {
        /*legacyScissor(graphics,
        *///? }
                scissorX - 1,
                fontY - 1,
                scissorX + width + 2,
                fontY + Minecraft.getInstance().font.lineHeight + 2
        );

        // DEBUG: Uncomment this line to see the scissor area
        // graphics.fill(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), 0x8F00FF00);

        //? if > 1.21.11 {
        graphics.text(Minecraft.getInstance().font, component, fontX, fontY, Color.WHITE.getRGB());
         //? } else
        //graphics.drawString(Minecraft.getInstance().font, component, fontX, fontY, Color.WHITE.getRGB());
        graphics.disableScissor();
        //? if > 1.21.1 {
        pose.popMatrix();
         //? } else {
        /*pose.popPose();
        *///? }
    }

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

    //? if <= 1.21.1 {
    /*// Math stuff.
    private static void legacyScissor(GuiGraphics graphics, int minX, int minY, int maxX, int maxY) {
        double mcScale = Minecraft.getInstance().getWindow().getGuiScale();
        float overlayScale = ModConfig.<Float>get("overlayScale") * 2.0f;

        graphics.enableScissor(
                (int) (minX * overlayScale / mcScale),
                (int) (minY * overlayScale / mcScale),
                (int) (maxX * overlayScale / mcScale),
                (int) (maxY * overlayScale / mcScale)
        );
    }
    *///? }
}
