package io.github.kabanfriends.craftgr.config.entry;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public class RadioStateListEntry extends TooltipListEntry {

    private final Button buttonWidget;
    private final List<AbstractWidget> widgets;

    public RadioStateListEntry(Component fieldName, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier, false);
        this.buttonWidget = Button.builder(Component.empty(), widget -> {
            widget.active = false;

            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
            HandlerState state = handler.getState();

            if (state == HandlerState.ACTIVE) {
                handler.stopPlayback();
            } else if (state != HandlerState.INITIALIZING) {
                AudioPlayerUtil.startPlaybackAsync();
            }
        }).bounds(0, 0, 150, 20).build();
        this.widgets = Lists.newArrayList(buttonWidget);
    }

    @Override
    public boolean isEdited() {
        return false;
    }

    @Override
    public void save() {
        //Radio state is not saved
    }

    @Override
    public String getValue() {
        throw new UnsupportedOperationException("Cannot get value of a RadioStateListEntry!");
    }

    @Override
    public Optional<String> getDefaultValue() {
        throw new UnsupportedOperationException("Cannot get default value of a RadioStateListEntry!");
    }

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.buttonWidget.active = isEditable() && isButtonActive();
        this.buttonWidget.setX(x + entryWidth - 150);
        this.buttonWidget.setY(y);
        this.buttonWidget.setMessage(getButtonText());
        Component displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, 16777215);
        } else {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), x, y + 6, getPreferredTextColor());
        }
        buttonWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    private boolean isButtonActive() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
        HandlerState state = handler.getState();

        return switch (state) {
            case NOT_INITIALIZED, STOPPED, READY, ACTIVE, FAIL -> true;
            case RELOADING, INITIALIZING -> false;
        };
    }

    private Component getButtonText() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
        HandlerState state = handler.getState();

        return switch (state) {
            case NOT_INITIALIZED, STOPPED -> Component.translatable("text.craftgr.config.option.playback.stopped");
            case RELOADING, INITIALIZING -> Component.translatable("text.craftgr.config.option.playback.connecting");
            case READY, ACTIVE -> Component.translatable("text.craftgr.config.option.playback.playing");
            case FAIL -> Component.translatable("text.craftgr.config.option.playback.fail");
        };
    }
}
