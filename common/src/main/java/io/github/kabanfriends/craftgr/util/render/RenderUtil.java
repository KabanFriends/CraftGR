package io.github.kabanfriends.craftgr.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import org.joml.Matrix4f;

public class RenderUtil {

    private static final float UI_BASE_SCALE = 1.0f;

    public static void setZLevelPre(PoseStack poseStack, int zLevel) {
        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, zLevel);
    }

    public static void setZLevelPost(PoseStack poseStack) {
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    public static void fill(PoseStack poseStack, float minX, float minY, float maxX, float maxY, int color, float opacity) {
        Matrix4f matrix4f = poseStack.last().pose();

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

        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = (float) (color >> 24 & 255) / 255.0F;

        a = a * opacity;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bb.addVertex(matrix4f, minX, maxY, 0.0F).setColor(r, g, b, a);
        bb.addVertex(matrix4f, maxX, maxY, 0.0F).setColor(r, g, b, a);
        bb.addVertex(matrix4f, maxX, minY, 0.0F).setColor(r, g, b, a);
        bb.addVertex(matrix4f, minX, minY, 0.0F).setColor(r, g, b, a);
        BufferUploader.drawWithShader(bb.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static float getUIScale(float uiScale) {
        double mcScale = CraftGR.getInstance().getMinecraft().getWindow().getGuiScale();

        return (float) ((((double) UI_BASE_SCALE) * (((double) UI_BASE_SCALE) / mcScale)) * uiScale);
    }

    public static void enableUnscaledScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.applyScissor(graphics.scissorStack.push(new UnscaledScreenRectangle(x, y, width - x, height -y)));
    }
}
