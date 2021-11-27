package io.github.kabanfriends.craftgr.render.impl;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.GRModMenu;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.render.Overlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.RenderUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SongInfoOverlay extends Overlay {

    private static final float BASE_SCALE = 1.0f;
    private static final int ALBUM_ART_SIZE = 105;
    private static final Identifier ALBUM_ART_PLACEHOLDER = new Identifier(CraftGR.MOD_ID, "textures/album_placeholder.png");

    private static SongInfoOverlay INSTANCE;

    private Map<String, Identifier> albumArts;

    public SongInfoOverlay() {
        INSTANCE = this;

        this.albumArts = new HashMap<>();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        if (CraftGR.MC.currentScreen == null) {
            if (GRConfig.getConfig().overlayVisibility != SongInfoOverlay.OverlayVisibility.ALWAYS) return;
        }else {
            if (GRConfig.getConfig().overlayVisibility == SongInfoOverlay.OverlayVisibility.NONE) return;
            if (GRConfig.getConfig().overlayVisibility == SongInfoOverlay.OverlayVisibility.CHAT && !(CraftGR.MC.currentScreen instanceof ChatScreen)) return;
        }

        Song currentSong = SongHandler.getInstance().song;

        if (currentSong != null) {
            TextRenderer font = CraftGR.MC.textRenderer;

            float scale = GRConfig.getConfig().overlayScale;
            matrix.scale(getUIScale(scale), getUIScale(scale), getUIScale(scale));

            int albumArtWidth;
            if (GRConfig.getConfig().hideAlbumArt) albumArtWidth = -6;
            else albumArtWidth = ALBUM_ART_SIZE;

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getConfig().overlayPosition, width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            RenderUtil.fill(matrix, x, y, x + width, y + ALBUM_ART_SIZE + 10 + 10, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);

            if (albumArts.containsKey(currentSong.albumArt) && !GRConfig.getConfig().hideAlbumArt) {
                Identifier albumArt = albumArts.get(currentSong.albumArt);

                if (albumArt == null) {
                    RenderUtil.bindTexture(ALBUM_ART_PLACEHOLDER);
                } else {
                    RenderUtil.bindTexture(albumArt);
                }
                DrawableHelper.drawTexture(matrix, x + 6, y + 6, 0f, 0f, ALBUM_ART_SIZE, ALBUM_ART_SIZE, ALBUM_ART_SIZE, ALBUM_ART_SIZE);
            }

            matrix.push();
            matrix.scale(2, 2, 2);

            if (currentSong.intermission) {
                DrawableHelper.drawTextWithShadow(matrix, CraftGR.MC.textRenderer, new TranslatableText("text.craftgr.song.intermission"), (x + 12 + 8 + albumArtWidth) / 2, (y + 8) / 2, Color.WHITE.getRGB());
            } else {
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, currentSong.title, (x + 12 + 8 + albumArtWidth) / 2, (y + 8) / 2, Color.WHITE.getRGB());
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, "(" + currentSong.year + ")", (x + 12 + 10 + albumArtWidth) / 2, (y + 8 + 20) / 2, Color.LIGHT_GRAY.getRGB());
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, currentSong.artist, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 2) / 2, Color.LIGHT_GRAY.getRGB());
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, currentSong.album, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 3) / 2, Color.LIGHT_GRAY.getRGB());
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, currentSong.circle, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 4) / 2, Color.LIGHT_GRAY.getRGB());
            }

            matrix.pop();

            if (currentSong.intermission) {
                RenderUtil.fill(matrix, x, y + ALBUM_ART_SIZE + 10 + 10, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            } else {
                long duration = currentSong.songEnd - currentSong.songStart;
                long played = System.currentTimeMillis() / 1000L - SongHandler.getInstance().songStart;
                if (played > duration) played = duration;

                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, getTimer((int) played), x + 6, y + ALBUM_ART_SIZE + 10, Color.WHITE.getRGB());

                int timerWidth = font.getWidth(getTimer((int) duration));
                DrawableHelper.drawStringWithShadow(matrix, CraftGR.MC.textRenderer, getTimer((int) duration), x + (int) width - timerWidth - 6, y + ALBUM_ART_SIZE + 10, Color.WHITE.getRGB());

                RenderUtil.fill(matrix, x, y + ALBUM_ART_SIZE + 10 + 10, x + (float) played / duration * width, y + height, GRConfig.getConfig().overlayBarColor + 0xFF000000, 0.6f);
                RenderUtil.fill(matrix, x + (float) played / duration * width, y + ALBUM_ART_SIZE + 10 + 10, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            }
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, CallbackInfo info) {
        OverlayVisibility visibility = GRConfig.getConfig().overlayVisibility;

        if (visibility == OverlayVisibility.NONE) return;
        if (visibility == OverlayVisibility.CHAT && !(CraftGR.MC.currentScreen instanceof ChatScreen)) return;

        Song currentSong = SongHandler.getInstance().song;

        if (currentSong != null && GRConfig.getConfig().openAlbum) {
            float scale = GRConfig.getConfig().overlayScale;

            float scaledX = mouseX / getUIScale(scale);
            float scaledY = mouseY / getUIScale(scale);

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getConfig().overlayPosition, width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            if (scaledX >= x && scaledX <= x + width && scaledY >= y && scaledY <= y + height) {
                boolean openScreen = true;
                if (CraftGR.MC.currentScreen instanceof ConfirmChatLinkScreen) openScreen = false;
                else if (CraftGR.MC.currentScreen instanceof LevelLoadingScreen) openScreen = false;
                else if (CraftGR.MC.currentScreen instanceof DownloadingTerrainScreen) openScreen = false;
                else if (CraftGR.MC.currentScreen instanceof ConnectScreen) openScreen = false;
                else if (CraftGR.MC.currentScreen instanceof SaveLevelScreen) openScreen = false;

                if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                    if (GRModMenu.isInModMenu()) openScreen = false;
                }
                if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                    if (GRConfig.isInConfig()) openScreen = false;
                }

                if (openScreen) {
                    String link = "https://gensokyoradio.net/music/album/" + currentSong.albumId;
                    Screen oldScreen = CraftGR.MC.currentScreen;

                    CraftGR.MC.openScreen(new ConfirmChatLinkScreen((result) -> {
                        if (result) Util.getOperatingSystem().open(link);
                        CraftGR.MC.openScreen(oldScreen);
                    }, link, true));

                    info.cancel();
                }
            }
        }
    }

    private float[] getOverlayCoordinate(OverlayPosition position, float width, float height) {
        float offset = 10 / GRConfig.getConfig().overlayScale;
        float x = CraftGR.MC.getWindow().getWidth() / GRConfig.getConfig().overlayScale - width - offset;
        float y = CraftGR.MC.getWindow().getHeight() / GRConfig.getConfig().overlayScale - height - offset;

        switch (position) {
            case TOP_RIGHT:
                return new float[]{x, offset};
            case BOTTOM_LEFT:
                return new float[]{offset, y};
            case BOTTOM_RIGHT:
                return new float[]{x, y};
            default:
                return new float[]{offset, offset};
        }
    }

    private float[] getOverlaySize() {
        Song currentSong = SongHandler.getInstance().song;

        TextRenderer font = CraftGR.MC.textRenderer;

        int maxWidth = 0;
        if (currentSong.intermission) {
            maxWidth = font.getWidth(currentSong.title);
        } else {
            String[] strings = {currentSong.title, currentSong.artist, currentSong.album, currentSong.circle};
            for (String string : strings) {
                int width = font.getWidth(string);
                if (width > maxWidth) maxWidth = width;
            }
        }

        int albumArtWidth;
        if (GRConfig.getConfig().hideAlbumArt) albumArtWidth = -6;
        else albumArtWidth = ALBUM_ART_SIZE;

        float width = 12 + 7 + albumArtWidth + (maxWidth * 2) + 10;
        float height = ALBUM_ART_SIZE + 6 + 20;

        return new float[]{width, height};
    }

    public void createAlbumArtTexture(Song song) {
        if (!albumArts.containsKey(song.albumArt)) {
            if (song.albumArt.isEmpty()) {
                albumArts.put(song.albumArt, null);
                return;
            }

            CraftGR.EXECUTOR.submit(() -> {
                Identifier albumArt = null;
                try {
                    Request request = new Request.Builder().url(GRConfig.getConfig().albumArtURL + song.albumArt).build();

                    Response response = CraftGR.HTTP_CLIENT.newCall(request).execute();
                    InputStream stream = response.body().byteStream();

                    //Wait for texture manager to be initialized
                    while (CraftGR.MC.getTextureManager() == null) {
                        Thread.sleep(1);
                    }

                    albumArt = CraftGR.MC.getTextureManager().registerDynamicTexture("craftgr_album", new NativeImageBackedTexture(NativeImage.read(stream)));
                } catch (Exception e) {
                    CraftGR.log(Level.ERROR, "Error while creating album art texture!");
                    e.printStackTrace();
                } finally {
                    albumArts.put(song.albumArt, albumArt);
                }
            });
        }
    }

    public enum OverlayPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public enum OverlayVisibility {
        NONE,
        ALWAYS,
        MENU,
        CHAT
    }

    private static String getTimer(int time) {
        int minutes = time / 60;
        int seconds = time % 60;

        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    private static float getUIScale(float uiScale) {
        double mcScale = CraftGR.MC.getWindow().getScaleFactor();

        return (float) ((((double) BASE_SCALE) * (((double) BASE_SCALE) / mcScale)) * uiScale);
    }

    public static SongInfoOverlay getInstance() {
        return INSTANCE;
    }
}
