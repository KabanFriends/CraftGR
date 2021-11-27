package io.github.kabanfriends.craftgr.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class RenderUtil {

    public static void bindTexture(Identifier texture, boolean depthTest) {
        CraftGR.MC.getTextureManager().bindTexture(texture);
        RenderSystem.enableBlend();
        if (depthTest) {
            RenderSystem.enableDepthTest();
        }
    }

    public static void bindTexture(Identifier texture) {
        bindTexture(texture, false);
    }

    public static void fill(MatrixStack matrix, float minX, float minY, float maxX, float maxY, int color, float opacity) {
        Matrix4f matrix4f = matrix.peek().getModel();

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

        BufferBuilder bb = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bb.begin(7, VertexFormats.POSITION_COLOR);
        bb.vertex(matrix4f, minX, maxY, 0.0F).color(r, g, b, a).next();
        bb.vertex(matrix4f, maxX, maxY, 0.0F).color(r, g, b, a).next();
        bb.vertex(matrix4f, maxX, minY, 0.0F).color(r, g, b, a).next();
        bb.vertex(matrix4f, minX, minY, 0.0F).color(r, g, b, a).next();
        bb.end();
        BufferRenderer.draw(bb);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
