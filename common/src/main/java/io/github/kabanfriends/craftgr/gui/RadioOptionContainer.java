package io.github.kabanfriends.craftgr.gui;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.ModUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RadioOptionContainer extends AbstractContainerWidget {

    private static final int CONFIG_BUTTON_SIZE = 20;
    private static final int CONFIG_BUTTON_PADDING = 4;

    private static final WidgetSprites CONFIG_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation(CraftGR.MOD_ID, "config"),
            new ResourceLocation(CraftGR.MOD_ID, "config_highlighted")
    );

    private final RadioVolumeSliderButton volumeSlider;
    private final ImageButton configButton;

    private final List<AbstractWidget> children;

    public RadioOptionContainer(int x, int y, int width) {
        super(x, y, width, 20, CommonComponents.EMPTY);
        boolean hasConfig = ModUtil.isConfigModAvailable();

        volumeSlider = new RadioVolumeSliderButton(x, y, hasConfig ? width - CONFIG_BUTTON_SIZE - CONFIG_BUTTON_PADDING : width);
        if (hasConfig) {
            configButton = new ImageButton(
                    x + width - CONFIG_BUTTON_SIZE,
                    y,
                    CONFIG_BUTTON_SIZE,
                    CONFIG_BUTTON_SIZE,
                    CONFIG_BUTTON_SPRITES,
                    (button) -> CraftGR.getPlatform().openConfigScreen()
            );
            children = List.of(volumeSlider, configButton);
        } else {
            configButton = null;
            children = List.of(volumeSlider);
        }
    }

    private void repositionChildren() {
        volumeSlider.setPosition(getX(), getY());
        if (configButton != null) {
            configButton.setPosition(getX() + width - CONFIG_BUTTON_SIZE, getY());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        repositionChildren();
        for (AbstractWidget widget : children) {
            widget.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractWidget widget : children) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (AbstractWidget widget : children) {
            if (widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (AbstractWidget widget : children) {
            if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
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
