package io.github.kabanfriends.craftgr.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.commands.Command;
import io.github.kabanfriends.craftgr.commands.arguments.ArgBuilder;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.song.Song;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class InfoCommand extends Command {

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        Song song = SongHandler.getInstance().song;

        LiteralText artist = new LiteralText(song.artist);
        artist.formatted(Formatting.DARK_GREEN);
        LiteralText title = new LiteralText(song.title);
        title.formatted(Formatting.GREEN);
        LiteralText titleConnector = new LiteralText(" - ");
        titleConnector.formatted(Formatting.DARK_GRAY);
        Text songName = artist.append(titleConnector).append(title);

        LiteralText year = new LiteralText("(" + song.year + ")");
        year.formatted(Formatting.GRAY);

        LiteralText albumName = new LiteralText(song.album);
        albumName.formatted(Formatting.AQUA);
        TranslatableText album = new TranslatableText("text.craftgr.command.info.album", albumName);
        Style albumStyle = Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://gensokyoradio.net/music/album/" + song.albumId))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("text.craftgr.command.info.album.tooltip")));
        album.setStyle(albumStyle);

        LiteralText circleName = new LiteralText(song.circle);
        circleName.formatted(Formatting.AQUA);
        TranslatableText circle = new TranslatableText("text.craftgr.command.info.circle", circleName);

        LiteralText ratingValue = new LiteralText(song.rating + "/5");
        ratingValue.formatted(Formatting.AQUA);
        TranslatableText rating = new TranslatableText("text.craftgr.command.info.rating", ratingValue);

        TranslatableText info = new TranslatableText("text.craftgr.command.info.header");
        CraftGR.MC.player.sendMessage(info, false);
        CraftGR.MC.player.sendMessage(songName, false);
        CraftGR.MC.player.sendMessage(year, false);
        CraftGR.MC.player.sendMessage(album, false);
        CraftGR.MC.player.sendMessage(circle, false);
        CraftGR.MC.player.sendMessage(rating, false);

        return 1;
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("info")
                .executes(InfoCommand::run)
        );
    }

}
