package sh.kaban.craftgr.rendering.gui;

import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.config.ModConfig;
import sh.kaban.craftgr.util.IdentifierUtil;
import sh.kaban.craftgr.util.ModUtil;
import net.minecraft.client.Minecraft;
//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//? if > 1.21.1 {
import net.minecraft.client.input.MouseButtonEvent;
//? }
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RadioOptionContainer extends AbstractContainerWidget {

    private static final int CONFIG_BUTTON_SIZE = 20;
    private static final int CONFIG_BUTTON_PADDING = 5;

    private static final Component BUTTON_NARRATION_NAME = Component.translatable("text.craftgr.button.config.narration");
    private static final Component DISABLED_TOOLTIP = Component.translatable("text.craftgr.button.config.disabled");

    private static final WidgetSprites CONFIG_BUTTON_SPRITES = new WidgetSprites(
            IdentifierUtil.thisMod("config"),
            IdentifierUtil.thisMod("config_disabled"),
            IdentifierUtil.thisMod("config_highlighted")
    );

    private final RadioVolumeSliderButton volumeSlider;
    private final ImageButton configButton;

    private final List<AbstractWidget> children;

    //? if > 1.21.1 {
    private GuiEventListener pressedChild;
    private int pressedButton = -1;
    //? }

    public RadioOptionContainer(int x, int y, int width) {
        //? if > 1.21.11 {
        super(x, y, width, 20, CommonComponents.EMPTY, AbstractScrollArea.defaultSettings(0));
        //? } else
        //super(x, y, width, 20, CommonComponents.EMPTY);

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

                    //? if > 1.21.11 {
                    minecraft.gui.setScreen(config.createScreen(minecraft.gui.screen()));
                    //? } else
                    //minecraft.setScreen(config.createScreen(minecraft.screen));
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
    //? if > 1.21.11 {
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
    //? } else
    //protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        repositionChildren();
        for (AbstractWidget widget : children) {
            //? if > 1.21.11 {
            widget.extractRenderState(graphics, mouseX, mouseY, delta);
            //? } else
            //widget.render(graphics, mouseX, mouseY, delta);
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
    public void setFocused(GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    //? if > 1.21.1 {
    @Override
    protected int contentHeight() {
        return 20;
    }

    @Override
    protected double scrollRate() {
        return 10.0;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        GuiEventListener child = getChildAt(event.x(), event.y()).orElse(null);
        clearPressedChild();

        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled && child != null) {
            pressedChild = child;
            pressedButton = event.button();
        }

        return handled;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (pressedChild != null && event.button() == pressedButton) {
            return pressedChild.mouseDragged(event, deltaX, deltaY);
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (pressedChild != null && event.button() == pressedButton) {
            GuiEventListener child = pressedChild;
            clearPressedChild();
            onRelease(event); // clears AbstractScrollArea's state
            return child.mouseReleased(event);
        }

        return super.mouseReleased(event);
    }

    private void clearPressedChild() {
        pressedChild = null;
        pressedButton = -1;
        setDragging(false);
    }
    //? }
}
