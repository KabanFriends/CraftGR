package io.github.kabanfriends.craftgr.config.entry.impl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import io.github.kabanfriends.craftgr.config.controller.RadioStateController;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

public class RadioStateConfigEntry extends GRConfigEntry<Boolean> {

    public RadioStateConfigEntry(String key) {
        super(key, false);
    }

    @Override
    public OptionProvider<Boolean> getOptionProvider() {
        return new OptionProvider<Boolean>() {
            @Override
            public Option<Boolean> getOption() {
                return Option.<Boolean>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller(RadioStateController.Builder::new)
                        .binding(getDefaultValue(), RadioStateConfigEntry.this::getValue, (value) -> {})
                        .build();
            }
        };
    }
}
