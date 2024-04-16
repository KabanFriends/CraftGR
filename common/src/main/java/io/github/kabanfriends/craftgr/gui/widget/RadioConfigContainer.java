package io.github.kabanfriends.craftgr.gui.widget;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RadioConfigContainer extends AbstractContainerWidget {

    private static final int CONFIG_BUTTON_SIZE = 20;
    private static final int CONFIG_BUTTON_PADDING = 4;

    private static final WidgetSprites CONFIG_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation(CraftGR.MOD_ID, "config"),
            new ResourceLocation(CraftGR.MOD_ID, "config_highlighted")
    );

    private final List<AbstractWidget> children;

    public RadioConfigContainer(int x, int y, int width) {
        super(x, y, width, 20, CommonComponents.EMPTY);

        this.children = new ArrayList<>();
        this.children.add(new RadioVolumeSliderButton(x, y, width));
        this.children.add(new ImageButton(
                x + width - CONFIG_BUTTON_SIZE - CONFIG_BUTTON_PADDING,
                y,
                CONFIG_BUTTON_SIZE,
                CONFIG_BUTTON_SIZE,
                CONFIG_BUTTON_SPRITES,
                (button) -> CraftGR.getPlatform().openConfigScreen()
        ));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        for (AbstractWidget widget : children) {
            widget.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        for (AbstractWidget widget : children) {
            if (widget.mouseClicked(d, e, i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }
}
