package sh.kaban.craftgr.rendering.element;

//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;

public abstract class RenderableElement {

    protected float x;
    protected float y;

    public RenderableElement(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    //? if > 1.21.11 {
    public abstract void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY);
    //? } else
    //public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);
}