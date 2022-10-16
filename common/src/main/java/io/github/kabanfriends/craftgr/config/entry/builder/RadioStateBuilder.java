package io.github.kabanfriends.craftgr.config.entry.builder;

import io.github.kabanfriends.craftgr.config.entry.RadioStateListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class RadioStateBuilder extends FieldBuilder {

    private Supplier<Optional<Component[]>> tooltipSupplier = Optional::empty;

    public RadioStateBuilder(Component fieldNameKey) {
        super(Component.empty(), fieldNameKey);
    }

    public RadioStateBuilder setTooltip(@Nullable Component... tooltip) {
        this.tooltipSupplier = () -> Optional.ofNullable(tooltip);
        return this;
    }

    @NotNull
    @Override
    public RadioStateListEntry build() {
        return new RadioStateListEntry(getFieldNameKey(), tooltipSupplier);
    }
}
