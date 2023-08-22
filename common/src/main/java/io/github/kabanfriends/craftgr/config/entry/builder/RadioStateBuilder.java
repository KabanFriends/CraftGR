package io.github.kabanfriends.craftgr.config.entry.builder;

import io.github.kabanfriends.craftgr.config.entry.RadioStateListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RadioStateBuilder extends AbstractFieldBuilder {

    private Supplier<Optional<Component[]>> tooltipSupplier = Optional::empty;

    public RadioStateBuilder(Component fieldNameKey) {
        super(Component.empty(), fieldNameKey);
    }

    @Override
    public RadioStateBuilder setTooltip(@Nullable Component... tooltip) {
        this.tooltipSupplier = () -> Optional.ofNullable(tooltip);
        return this;
    }

    @Override
    public RadioStateBuilder setSaveConsumer(Consumer saveConsumer) {
        return this;
    }

    @NotNull
    @Override
    public RadioStateListEntry build() {
        return new RadioStateListEntry(getFieldNameKey(), tooltipSupplier);
    }
}
