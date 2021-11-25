package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.commands.Command;
import io.github.kabanfriends.craftgr.commands.impl.InfoCommand;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private static List<Command> cmds = new ArrayList<>();

    public static List<Command> getCommands() {
        return cmds;
    }

    public CommandHandler() {
        register(
                new InfoCommand()
        );
    }

    public void register(Command cmd) {
        cmd.register(CraftGR.MC, ClientCommandManager.DISPATCHER);
        cmds.add(cmd);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}
