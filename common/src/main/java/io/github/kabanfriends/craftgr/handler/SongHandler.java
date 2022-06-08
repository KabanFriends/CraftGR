package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.InitState;
import io.github.kabanfriends.craftgr.util.ProcessResult;
import io.github.kabanfriends.craftgr.util.TitleFixer;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SongHandler {

    private static final SongHandler INSTANCE = new SongHandler();

    private static InitState initState = InitState.NOT_INITIALIZED;

    private boolean destroyed;
    private Song song;
    private long songStart;
    private long songEnd;

    public void initialize() {
        CraftGR.EXECUTOR.submit(() -> {
            ProcessResult result = prepareNewSong();
            if (result == ProcessResult.ERROR) {
                initState = InitState.FAIL;
            } else {
                initState = InitState.SUCCESS;
            }

            this.start();
        });
    }

    private ProcessResult prepareNewSong() {
        Song song;
        try {
            song = getSongFromInfoXML(GRConfig.getConfig().url.songInfoURL);
        } catch (Exception e) {
            CraftGR.log(Level.ERROR, "Error while fetching song information!");
            e.printStackTrace();

            return ProcessResult.ERROR;
        }
        this.song = song;

        SongInfoOverlay.getInstance().createAlbumArtTexture(song);
        return ProcessResult.SUCCESS;
    }

    private void start() {
        while (initState == InitState.SUCCESS) {
            if (this.song != null) {
                if (System.currentTimeMillis() / 1000L > this.getSongEnd()) {
                    ProcessResult result = prepareNewSong();
                    if (result == ProcessResult.ERROR) {
                        break;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) { }
        }

        CraftGR.log(Level.ERROR, "Error in displaying song information! Fetching again in 30 seconds...");
        this.song = null;
        try {
            Thread.sleep(30 * 1000L);
        } catch (InterruptedException e) { }

        this.initialize();
    }

    public void destroy() {
        destroyed = true;
    }

    private Song getSongFromInfoXML(String url) throws ParserConfigurationException, IOException, SAXException {
        Request request = new Request.Builder().url(url).build();

        Response response = CraftGR.getHttpClient().newCall(request).execute();
        InputStream stream = response.body().byteStream();

        BufferedReader r = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }

        Document document = loadXMLFromString(sb.toString());
        Node node = document.getDocumentElement();

        Song song = new Song();

        for (Node c1 = node.getFirstChild(); c1 != null; c1 = c1.getNextSibling()) {
            switch (c1.getNodeName()) {
                case "SONGINFO":
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        String content = c2.getTextContent();
                        switch (c2.getNodeName()) {
                            case "TITLE":
                                song.title = TitleFixer.fixJapaneseString(content);
                                break;
                            case "ARTIST":
                                song.artist = TitleFixer.fixJapaneseString(content);
                                break;
                            case "ALBUM":
                                song.album = TitleFixer.fixJapaneseString(content);
                                break;
                            case "YEAR":
                                song.year = content;
                                break;
                            case "CIRCLE":
                                song.circle = TitleFixer.fixJapaneseString(content);
                        }
                    }
                    break;
                case "SONGTIMES":
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        String content = c2.getTextContent();
                        switch (c2.getNodeName()) {
                            case "DURATION":
                                if (content.equals("0")) song.setIntermission(true);
                                break;
                            case "SONGSTART":
                                song.songStart = Long.parseLong(content);
                                break;
                            case "SONGEND":
                                song.songEnd = Long.parseLong(content);
                        }
                    }
                    break;
                case "SONGDATA":
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        String content = c2.getTextContent();
                        switch (c2.getNodeName()) {
                            case "ALBUMID":
                                song.albumId = Integer.parseInt(content);
                                break;
                            case "RATING":
                                song.rating = Float.parseFloat(content);
                        }
                    }
                    break;
                case "MISC":
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        String content = c2.getTextContent();
                        switch (c2.getNodeName()) {
                            case "ALBUMART":
                                song.albumArt = content;
                                break;
                            case "OFFSETTIME":
                                song.offsetTime = Long.parseLong(content);
                        }
                    }
            }
        }

        long played = song.offsetTime - song.songStart;
        long duration = song.songEnd - song.songStart;
        this.songStart = System.currentTimeMillis() / 1000L - played;
        this.songEnd = this.songStart + duration;

        if (song.isIntermission()) {
            song.albumArt = "";
            song.title = "";

            this.songEnd = System.currentTimeMillis() / 1000L + 4L;
        }

        response.close();

        return song;
    }

    private static Document loadXMLFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ReaderInputStream(new StringReader(xml), StandardCharsets.UTF_8);
        return builder.parse(is);
    }

    public Song getCurrentSong() {
        return song;
    }

    public long getSongStart() {
        return songStart;
    }

    public long getSongEnd() {
        return songEnd;
    }

    public static SongHandler getInstance() {
        return INSTANCE;
    }
}
