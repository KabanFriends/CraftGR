package io.github.kabanfriends.craftgr.util.render;

import com.mojang.blaze3d.vertex.*;
import io.github.kabanfriends.craftgr.mixin.MixinAccessorGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class RenderUtil {

    private static final float UI_BASE_SCALE = 1.0f;

    public static void setZLevelPre(PoseStack poseStack, int zLevel) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, zLevel);
    }

    public static void setZLevelPost(PoseStack poseStack) {
        poseStack.popPose();
    }

    public static void fill(GuiGraphics graphics, RenderType renderType, float minX, float minY, float maxX, float maxY, int color) {
        Matrix4f matrix4f = graphics.pose().last().pose();

        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        VertexConsumer vertexConsumer = ((MixinAccessorGuiGraphics) graphics).getBufferSource().getBuffer(renderType);
        vertexConsumer.addVertex(matrix4f, minX, maxY, 0.0F).setColor(color);
        vertexConsumer.addVertex(matrix4f, maxX, maxY, 0.0F).setColor(color);
        vertexConsumer.addVertex(matrix4f, maxX, minY, 0.0F).setColor(color);
        vertexConsumer.addVertex(matrix4f, minX, minY, 0.0F).setColor(color);
    }

    public static float getUIScale(float uiScale) {
        double mcScale = Minecraft.getInstance().getWindow().getGuiScale();

        return (float) ((((double) UI_BASE_SCALE) * (((double) UI_BASE_SCALE) / mcScale)) * uiScale);
    }

    public static void enableUnscaledScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.applyScissor(graphics.scissorStack.push(new UnscaledScreenRectangle(x, y, width, height)));
    }
}
