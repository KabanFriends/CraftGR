package io.github.kabanfriends.craftgr.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.kabanfriends.craftgr.commands.Command;
import io.github.kabanfriends.craftgr.commands.arguments.ArgBuilder;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.MessageUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ReconnectCommand extends Command {

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        MessageUtil.sendTranslatableMessage("text.craftgr.message.reconnect");
        AudioPlayerHandler.getInstance().player.stop();

        return 1;
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("info")
                .executes(ReconnectCommand::run)
        );
    }
}
