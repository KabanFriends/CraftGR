package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.TitleFixer;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SongHandler {

    private static SongHandler INSTANCE;
    private static boolean INIT_FAILED;
    private boolean destroyed;

    public Song song;
    private long songEnd;

    public long songStart;

    public SongHandler() {
        INSTANCE = this;

        CraftGR.EXECUTOR.submit(() -> {
            try {
                this.prepareNewSong();
            } catch (Exception e) {
                INIT_FAILED = true;
            }

            this.start();
        });
    }

    private void prepareNewSong() {
        this.song = getSongFromInfoXML(GRConfig.getConfig().url.songInfoURL);

        SongInfoOverlay.getInstance().createAlbumArtTexture(song);
    }

    private void start() {
        if (!INIT_FAILED) {
            while (!destroyed) {
                try {

                    if (this.song != null) {
                        if (System.currentTimeMillis() / 1000L > this.songEnd) {
                            prepareNewSong();
                        }
                    }

                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void destroy() {
        destroyed = true;
    }

    private Song getSongFromInfoXML(String url) {
        try {
            Request request = new Request.Builder().url(url).build();

            Response response = CraftGR.HTTP_CLIENT.newCall(request).execute();
            InputStream stream = response.body().byteStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }

            Document document = loadXMLFromString(sb.toString());
            Node node = document.getDocumentElement();

            Song song = new Song();

            for (Node c1 = node.getFirstChild(); c1 != null; c1 = c1.getNextSibling()) {
                if (c1.getNodeName().equals("SONGINFO")) {
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        if (c2.getNodeName().equals("TITLE")) song.title = TitleFixer.fixJapaneseString(c2.getTextContent());
                        else if (c2.getNodeName().equals("ARTIST")) song.artist = TitleFixer.fixJapaneseString(c2.getTextContent());
                        else if (c2.getNodeName().equals("ALBUM")) song.album = TitleFixer.fixJapaneseString(c2.getTextContent());
                        else if (c2.getNodeName().equals("YEAR")) song.year = c2.getTextContent();
                        else if (c2.getNodeName().equals("CIRCLE")) song.circle = TitleFixer.fixJapaneseString(c2.getTextContent());
                    }
                } else if (c1.getNodeName().equals("SONGTIMES")) {
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        if (c2.getNodeName().equals("DURATION") && c2.getTextContent().equals("0"))
                            song.intermission = true;
                        else if (c2.getNodeName().equals("SONGSTART"))
                            song.songStart = Long.parseLong(c2.getTextContent());
                        else if (c2.getNodeName().equals("SONGEND")) song.songEnd = Long.parseLong(c2.getTextContent());
                    }
                } else if (c1.getNodeName().equals("SONGDATA")) {
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        if (c2.getNodeName().equals("ALBUMID")) song.albumId = Integer.parseInt(c2.getTextContent());
                        else if (c2.getNodeName().equals("RATING")) song.rating = Float.parseFloat(c2.getTextContent());
                    }
                } else if (c1.getNodeName().equals("MISC")) {
                    for (Node c2 = c1.getFirstChild(); c2 != null; c2 = c2.getNextSibling()) {
                        if (c2.getNodeName().equals("ALBUMART")) song.albumArt = c2.getTextContent();
                        else if (c2.getNodeName().equals("OFFSETTIME"))
                            song.offsetTime = Long.parseLong(c2.getTextContent());
                    }
                }
            }

            long played = song.offsetTime - song.songStart;
            long duration = song.songEnd - song.songStart;
            this.songStart = System.currentTimeMillis() / 1000L - played;
            this.songEnd = this.songStart + duration;

            if (song.intermission) {
                song.albumArt = "";
                song.title = "";

                this.songEnd = System.currentTimeMillis() / 1000L + 4L;
            }

            response.close();

            return song;
        } catch (Exception e) {
            CraftGR.log(Level.ERROR, "Error while fetching song information!");
            e.printStackTrace();

            return null;
        }
    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ReaderInputStream(new StringReader(xml), StandardCharsets.UTF_8);
        return builder.parse(is);
    }

    public static SongHandler getInstance() {
        return INSTANCE;
    }
}
