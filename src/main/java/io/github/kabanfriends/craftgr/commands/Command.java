package io.github.kabanfriends.craftgr.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public abstract class Command {
    public abstract void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd);
}
