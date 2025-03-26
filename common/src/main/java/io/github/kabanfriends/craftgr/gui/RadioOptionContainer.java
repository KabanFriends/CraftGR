package io.github.kabanfriends.craftgr.gui;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadioOptionContainer extends AbstractContainerWidget {

    private static final int CONFIG_BUTTON_SIZE = 20;
    private static final int CONFIG_BUTTON_PADDING = 5;

    private static final Component BUTTON_NARRATION_NAME = Component.translatable("text.craftgr.button.config.narration");
    private static final Component DISABLED_TOOLTIP = Component.translatable("text.craftgr.button.config.disabled");

    private static final WidgetSprites CONFIG_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "config"),
            ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "config_disabled"),
            ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "config_highlighted")
    );

    private final RadioVolumeSliderButton volumeSlider;
    private final ImageButton configButton;

    private final List<AbstractWidget> children;

    public RadioOptionContainer(int x, int y, int width) {
        super(x, y, width, 20, CommonComponents.EMPTY);

        volumeSlider = new RadioVolumeSliderButton(x, y, width - CONFIG_BUTTON_SIZE - CONFIG_BUTTON_PADDING);
        configButton = new ImageButton(
                x + width - CONFIG_BUTTON_SIZE,
                y,
                CONFIG_BUTTON_SIZE,
                CONFIG_BUTTON_SIZE,
                CONFIG_BUTTON_SPRITES,
                (button) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    ModConfig config = CraftGR.getInstance().getConfig();
                    minecraft.setScreen(config.createScreen(minecraft.screen));
                },
                BUTTON_NARRATION_NAME
        );

        if (!ModUtil.isConfigModAvailable()) {
            configButton.active = false;
            configButton.setTooltip(Tooltip.create(DISABLED_TOOLTIP));
        }

        children = List.of(volumeSlider, configButton);
    }

    private void repositionChildren() {
        volumeSlider.setPosition(getX(), getY());
        configButton.setPosition(getX() + width - CONFIG_BUTTON_SIZE, getY());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        for (AbstractWidget widget : children) {
            if (widget.isFocused()) {
                widget.updateNarration(output);
            }
        }
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        repositionChildren();
        for (AbstractWidget widget : children) {
            widget.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (getFocused() != null) {
            getFocused().setFocused(focused);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    protected int contentHeight() {
        return 20;
    }

    @Override
    protected double scrollRate() {
        return 10.0;
    }
}
