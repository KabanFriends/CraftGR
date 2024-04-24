package io.github.kabanfriends.craftgr.config.entry;

import dev.isxander.yacl3.api.Option;

// This class is required to prevent YACL classes from being loaded
// from lambda expressions when YACL is not installed
public interface OptionProvider<T> {

    Option<T> getOption();
}
