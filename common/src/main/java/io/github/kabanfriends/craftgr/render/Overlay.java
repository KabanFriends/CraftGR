package io.github.kabanfriends.craftgr.render;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class Overlay {

    abstract public void render(PoseStack poseStack, int mouseX, int mouseY);

    abstract public boolean onMouseClick(int mouseX, int mouseY);

}
