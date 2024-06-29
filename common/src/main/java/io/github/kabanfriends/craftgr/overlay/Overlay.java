package io.github.kabanfriends.craftgr.overlay;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Overlay {

    private boolean active;

    public Overlay() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    public abstract boolean mouseClick(int mouseX, int mouseY);
}
