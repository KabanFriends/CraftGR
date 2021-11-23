package io.github.kabanfriends.craftgr.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.commands.Command;
import io.github.kabanfriends.craftgr.commands.arguments.ArgBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.MessageUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.commons.io.input.ReaderInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class InfoCommand extends Command {

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        CraftGR.EXECUTOR.submit(() -> {
            try {
                URLConnection connection = new URL(GRConfig.getConfig().songInfoURL).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();

                BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }

                Document document = loadXMLFromString(sb.toString());
                Node node = document.getDocumentElement();

                Song song = new Song();

                for(Node c1 = node.getFirstChild(); c1 != null; c1 = c1.getNextSibling()) {
                    if (c1.getNodeName().equals("SONGINFO")) {
                        for(Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                            if (c2.getNodeName().equals("TITLE")) song.title = c2.getTextContent();
                            else if (c2.getNodeName().equals("ARTIST")) song.artist = c2.getTextContent();
                            else if (c2.getNodeName().equals("ALBUM")) song.album = c2.getTextContent();
                            else if (c2.getNodeName().equals("YEAR")) song.year = c2.getTextContent();
                            else if (c2.getNodeName().equals("CIRCLE")) song.circle = c2.getTextContent();
                        }
                    }
                    else if (c1.getNodeName().equals("SONGDATA")) {
                        for(Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                            if (c2.getNodeName().equals("ALBUMID")) song.albumId = Integer.parseInt(c2.getTextContent());
                            else if (c2.getNodeName().equals("RATING")) song.rating = Float.parseFloat(c2.getTextContent());
                        }
                    }
                }

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

            }catch (Exception e) {
                MessageUtil.sendTranslatableMessage("text.craftgr.command.info.error");
                e.printStackTrace();
            }
        });

        return 1;
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("info")
                .executes(InfoCommand::run)
        );
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ReaderInputStream(new StringReader(xml), StandardCharsets.UTF_8);
        return builder.parse(is);
    }
}
