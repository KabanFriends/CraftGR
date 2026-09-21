package sh.kaban.craftgr.rendering.overlay;

//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;

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

    //? if > 1.21.11 {
    public abstract void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY);
    //? } else
    //public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    public abstract boolean mouseClick(int mouseX, int mouseY);
}
