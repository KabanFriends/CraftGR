package sh.kaban.craftgr.util;

//? if <= 1.21.1 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
*///? }
import net.minecraft.client.Minecraft;

public class RenderUtil {

    private static final float UI_BASE_SCALE = 1.0f;

    //? if <= 1.21.1 {
    /*public static void setZLevelPre(PoseStack poseStack, int zLevel) {
        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, zLevel);
    }

    public static void setZLevelPost(PoseStack poseStack) {
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }
    *///? }

    public static float getUIScale(float uiScale) {
        double mcScale = Minecraft.getInstance().getWindow().getGuiScale();

        return (float) ((((double) UI_BASE_SCALE) * (((double) UI_BASE_SCALE) / mcScale)) * uiScale);
    }
}
