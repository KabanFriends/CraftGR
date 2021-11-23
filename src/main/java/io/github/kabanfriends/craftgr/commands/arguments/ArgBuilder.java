package io.github.kabanfriends.craftgr.commands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public final class ArgBuilder {

    private ArgBuilder() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgument.literal(name);
    }

    public static <T> RequiredArgument<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgument.argument(name, type);
    }

    public static <T> Command<T> onExecute(Command<T> cmd) {
        return context -> {
            try {
                cmd.run(context);
                return 1;
            } catch (Throwable e) {
                e.printStackTrace();
                return -1;
            }
        };
    }

}
