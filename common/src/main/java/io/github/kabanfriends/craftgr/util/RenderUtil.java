package io.github.kabanfriends.craftgr.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderUtil {

    public static void setZLevelPre(PoseStack poseStack, int zLevel) {
        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, zLevel);
    }

    public static void setZLevelPost(PoseStack poseStack) {
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    public static void bindTexture(ResourceLocation texture, boolean depthTest) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        if (depthTest) {
            RenderSystem.enableDepthTest();
        }
    }

    public static void bindTexture(ResourceLocation texture) {
        bindTexture(texture, false);
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

        BufferBuilder bb = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bb.vertex(matrix4f, minX, maxY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, maxX, maxY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, maxX, minY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, minX, minY, 0.0F).color(r, g, b, a).endVertex();
        bb.end();
        BufferUploader.end(bb);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
