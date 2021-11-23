package io.github.kabanfriends.craftgr.commands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class LiteralArgument<S> extends LiteralArgumentBuilder<S> {

    protected LiteralArgument(String literal) {
        super(literal);
    }

    public static <S> LiteralArgument<S> literal(final String name) {
        return new LiteralArgument<>(name);
    }

    @Override
    public LiteralArgumentBuilder<S> executes(Command<S> command) {
        return super.executes(ArgBuilder.onExecute(command));
    }
}
