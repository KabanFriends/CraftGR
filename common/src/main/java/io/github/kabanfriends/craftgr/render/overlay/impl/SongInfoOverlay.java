package io.github.kabanfriends.craftgr.render.overlay.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.YACLScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.render.overlay.Overlay;
import io.github.kabanfriends.craftgr.render.widget.impl.ScrollingText;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.HandlerState;
import io.github.kabanfriends.craftgr.util.HttpUtil;
import io.github.kabanfriends.craftgr.util.RenderUtil;
import io.github.kabanfriends.craftgr.util.ResponseHolder;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SongInfoOverlay extends Overlay {

    //<editor-fold desc="UI size constants">
    public static final int ART_TOP_PADDING = 6;
    public static final int ART_BOTTOM_PADDING = 14;
    public static final int ART_LEFT_PADDING = 6;
    public static final int ART_INFO_SPACE_WIDTH = 12;
    public static final int ART_SIZE = 106;

    public static final int INFO_TOP_PADDING = 8;
    public static final int INFO_RIGHT_PADDING = 6;
    public static final int INFO_LINE_HEIGHT = 20;
    public static final int YEAR_ARTIST_SPACE_HEIGHT = 7;

    public static final int TIMER_RIGHT_PADDING = 6;
    public static final int ART_TIMER_SPACE_HEIGHT = 4;

    public static final int MUTED_ICON_TOP_PADDING = 8;
    public static final int MUTED_ICON_RIGHT_PADDING = 6;
    public static final int TITLE_MUTED_ICON_SPACE = 6;
    public static final int MUTED_ICON_SIZE = 16;

    public static final int PROGRESS_BAR_HEIGHT = 6;
    //</editor-fold>

    private static final int ALBUM_ART_TEXTURE_SIZE = 512;

    private static final int ALBUM_ART_FETCH_TRIES = 3;
    private static final int ALBUM_ART_FETCH_DELAY_SECONDS = 4;

    private static final ResourceLocation ALBUM_ART_PLACEHOLDER_LOCATION = new ResourceLocation(CraftGR.MOD_ID, "textures/album_placeholder.png");
    private static final ResourceLocation ALBUM_ART_LOCATION = new ResourceLocation(CraftGR.MOD_ID, "album");

    private static SongInfoOverlay instance;

    private final TextureManager textureManager;

    private DynamicTexture albumArtTexture;
    private ScrollingText songTitleText;
    private boolean hasAlbumArt;
    private boolean expanded;
    private boolean muted;

    public SongInfoOverlay(TextureManager textureManager) {
        SongInfoOverlay.instance = this;

        this.textureManager = textureManager;
        this.expanded = false;
        this.songTitleText = new ScrollingText(0, 0, Component.empty());

        updateScrollWidth();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        OverlayVisibility visibility = GRConfig.getValue("overlayVisibility");

        if (CraftGR.MC.screen == null) {
            if (visibility != SongInfoOverlay.OverlayVisibility.ALWAYS) return;
        } else {
            if (visibility == SongInfoOverlay.OverlayVisibility.NONE) return;
            if (visibility == SongInfoOverlay.OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return;
        }

        Song currentSong = SongHandler.getInstance().getCurrentSong();

        if (currentSong != null) {
            Font font = CraftGR.MC.font;

            float scale = GRConfig.getValue("overlayScale");

            int albumArtWidth = GRConfig.getValue("hideAlbumArt") ? -ART_LEFT_PADDING : ART_SIZE;

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getValue("overlayPosition"), width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            //Rendering
            PoseStack poseStack = graphics.pose();

            RenderUtil.setZLevelPre(poseStack, 400);
            poseStack.scale(RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale));

            RenderUtil.fill(poseStack, x, y, x + width, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            if (!GRConfig.<Boolean>getValue("hideAlbumArt")) {
                graphics.blit(hasAlbumArt ? ALBUM_ART_LOCATION : ALBUM_ART_PLACEHOLDER_LOCATION, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
            }

            poseStack.pushPose();
            poseStack.scale(2, 2, 2);

            if (!currentSong.isIntermission()) {
                int dotWidth = font.width("...");

                String year = null;
                if (currentSong.year != null) {
                    year = "(" + currentSong.year + ")";
                }

                String[] strings = {year, currentSong.artist, currentSong.album, currentSong.circle};
                for (int i = 0; i < 4; i++) {
                    String str = strings[i];
                    if (str != null) {
                        if (!expanded) {
                            int textWidth = font.width(str);
                            if (textWidth > GRConfig.<Integer>getValue("overlayWidth")) {
                                str = font.plainSubstrByWidth(str, GRConfig.<Integer>getValue("overlayWidth") - dotWidth);
                                if (!str.equals(strings[i])) {
                                    str += "...";
                                }
                            }
                        }

                        graphics.drawString(CraftGR.MC.font, str, (x + ART_LEFT_PADDING + ART_INFO_SPACE_WIDTH + albumArtWidth) / 2, (y + INFO_TOP_PADDING + (i > 0 ? YEAR_ARTIST_SPACE_HEIGHT : 0) + INFO_LINE_HEIGHT * (i + 1)) / 2, Color.LIGHT_GRAY.getRGB());
                    }
                }
            }

            HandlerState state = AudioPlayerHandler.getInstance().getState();
            if (state == HandlerState.STOPPED || state == HandlerState.FAIL) {
                if (!muted) {
                    muted = true;
                    updateScrollWidth();
                }
                graphics.drawString(CraftGR.MC.font, CraftGR.AUDIO_MUTED_ICON, (x + (int)width - MUTED_ICON_RIGHT_PADDING - MUTED_ICON_SIZE) / 2, (y + MUTED_ICON_TOP_PADDING) / 2, Color.WHITE.getRGB());
            } else if (muted) {
                muted = false;
                updateScrollWidth();
            }

            poseStack.popPose();

            if (currentSong.isIntermission()) {
                RenderUtil.fill(poseStack, x, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
            } else {
                long duration = currentSong.songEnd - currentSong.songStart;
                long played = System.currentTimeMillis() / 1000L - SongHandler.getInstance().getSongStart();
                if (played > duration) played = duration;

                graphics.drawString(CraftGR.MC.font, getTimer((int) played), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, Color.WHITE.getRGB());

                int timerWidth = font.width(getTimer((int) duration));
                graphics.drawString(CraftGR.MC.font, getTimer((int) duration), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, Color.WHITE.getRGB());

                RenderUtil.fill(poseStack, x, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + (float) played / duration * width, y + height, GRConfig.<Color>getValue("overlayBarColor").getRGB() + 0xFF000000, 0.6f);
                RenderUtil.fill(poseStack, x + (float) played / duration * width, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
            }

            songTitleText.setX(x + ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH);
            songTitleText.setY(y + INFO_TOP_PADDING);
            songTitleText.render(graphics, mouseX, mouseY);

            RenderUtil.setZLevelPost(poseStack);

            //Mouse hover detection
            float mouseScaledX = mouseX / RenderUtil.getUIScale(scale);
            float mouseScaledY = mouseY / RenderUtil.getUIScale(scale);

            if (mouseScaledX >= x && mouseScaledX <= x + width && mouseScaledY >= y && mouseScaledY <= y + height) {
                if (!expanded) {
                    expanded = true;
                    updateScrollWidth();
                }
            } else if (expanded) {
                expanded = false;
                updateScrollWidth();
            }
        }
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY) {
        if (CraftGR.MC.screen instanceof ConfirmLinkScreen) return true;
        if (CraftGR.MC.screen instanceof LevelLoadingScreen) return true;
        if (CraftGR.MC.screen instanceof ReceivingLevelScreen) return true;
        if (CraftGR.MC.screen instanceof ProgressScreen) return true;
        if (CraftGR.MC.screen instanceof ConnectScreen) return true;
        if (CraftGR.MC.screen instanceof GenericDirtMessageScreen) return true;

        if (CraftGR.getPlatform().isInModMenu()) return true;

        if (CraftGR.MC.screen instanceof YACLScreen) return true;

        OverlayVisibility visibility = GRConfig.getValue("overlayVisibility");

        if (visibility == OverlayVisibility.NONE) return true;
        if (visibility == OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return true;

        Song currentSong = SongHandler.getInstance().getCurrentSong();

        if (currentSong != null && GRConfig.<Boolean>getValue("openAlbum")) {
            if (currentSong.isIntermission()) return true;

            float scale = GRConfig.getValue("overlayScale");

            float scaledX = mouseX / RenderUtil.getUIScale(scale);
            float scaledY = mouseY / RenderUtil.getUIScale(scale);

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getValue("overlayPosition"), width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            if (scaledX >= x && scaledX <= x + width && scaledY >= y && scaledY <= y + height) {
                String link = "https://gensokyoradio.net/music/album/" + currentSong.albumId;
                Screen oldScreen = CraftGR.MC.screen;

                CraftGR.MC.setScreen(new ConfirmLinkScreen((result) -> {
                    if (result) Util.getPlatform().openUri(link);
                    CraftGR.MC.setScreen(oldScreen);
                }, link, true));

                return false;
            }
        }
        return true;
    }

    private float[] getOverlayCoordinate(OverlayPosition position, float width, float height) {
        float offset = 10 / GRConfig.<Float>getValue("overlayScale");
        float x = CraftGR.MC.getWindow().getWidth() / GRConfig.<Float>getValue("overlayScale") - width - offset;
        float y = CraftGR.MC.getWindow().getHeight() / GRConfig.<Float>getValue("overlayScale") - height - offset;

        // TODO: Improve or remove
        /*
        if (CraftGR.MC.screen instanceof YACLScreen) {
            float overlayScale = GRConfig.<Float>getValue("overlayScale");
            float guiScale = (float)CraftGR.MC.getWindow().getGuiScale();
            return new float[] {guiScale * 8 / overlayScale, guiScale * 28 / overlayScale};
        }
        */

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
        int albumArtWidth;
        if (GRConfig.getValue("hideAlbumArt")) albumArtWidth = -ART_LEFT_PADDING;
        else albumArtWidth = ART_SIZE;

        float width;
        float height = ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING + PROGRESS_BAR_HEIGHT;

        if (expanded) {
            int maxWidth = getMaxTextWidth();

            if (GRConfig.<Integer>getValue("overlayWidth") > maxWidth) {
                maxWidth = GRConfig.getValue("overlayWidth");
            }

            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + maxWidth * 2 + INFO_RIGHT_PADDING;
        } else {
            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + GRConfig.<Integer>getValue("overlayWidth") * 2 + INFO_RIGHT_PADDING;
        }

        return new float[]{width, height};
    }

    private int getMaxTextWidth() {
        Song currentSong = SongHandler.getInstance().getCurrentSong();
        Font font = CraftGR.MC.font;
        int maxWidth = 0;

        if (currentSong.isIntermission()) {
            maxWidth = font.width(Component.translatable("text.craftgr.song.intermission"));
        } else {
            String[] strings = {currentSong.title, "(" + currentSong.year + ")", currentSong.artist, currentSong.album, currentSong.circle};
            for (String string : strings) {
                if (string != null) {
                    int textWidth = font.width(string);
                    if (textWidth > maxWidth) {
                        maxWidth = textWidth;
                    }
                }
            }
        }

        return maxWidth;
    }

    public void setIntermissionSongTitle() {
        songTitleText.setText(Component.translatable("text.craftgr.song.intermission"));
        songTitleText.resetScroll();
    }

    public void setSongTitle(String title) {
        songTitleText.setText(Component.literal(title));
        songTitleText.resetScroll();
    }

    public void updateScrollWidth() {
        int width = GRConfig.getValue("overlayWidth");
        if (expanded) {
            width = getMaxTextWidth();

            if (GRConfig.<Integer>getValue("overlayWidth") > width) {
                width = GRConfig.getValue("overlayWidth");
            }
        }
        if (muted) {
            width -= (MUTED_ICON_SIZE + TITLE_MUTED_ICON_SPACE) / 2;
        }
        songTitleText.setWidth(width);
        songTitleText.resetScroll();
    }

    public void createAlbumArtTexture(Song song) {
        hasAlbumArt = false;
        if (song.albumArt == null || song.albumArt.isEmpty()) {
            return;
        }

        String url = GRConfig.getValue("urlAlbumArt") + song.albumArt;

        int tries = 0;
        do {
            tries++;

            CraftGR.bypassPngValidation = true;

            try {
                HttpGet get = HttpUtil.get(url);

                try (
                        ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
                        InputStream stream = resizeImage(response.getResponse().getEntity().getContent())
                ) {
                    NativeImage image = NativeImage.read(stream);

                    if (albumArtTexture == null) {
                        albumArtTexture = new DynamicTexture(image);
                    } else {
                        albumArtTexture.setPixels(image);
                        albumArtTexture.upload();
                    }

                    // OptiFine compatibility: RenderSystem works only in the main thread
                    CraftGR.MC.execute(() -> {
                        textureManager.register(ALBUM_ART_LOCATION, albumArtTexture);
                        hasAlbumArt = true;
                    });
                }
                break;
            } catch (Exception e) {
                CraftGR.log(Level.ERROR, "Error while creating album art texture! (" + url + ")");
                e.printStackTrace();

                if (albumArtTexture != null) {
                    textureManager.release(ALBUM_ART_LOCATION);
                    albumArtTexture.close();
                    albumArtTexture = null;
                }
            } finally {
                CraftGR.bypassPngValidation = false;
            }

            if (tries < ALBUM_ART_FETCH_TRIES) {
                CraftGR.log(Level.INFO, "Retrying to create album art texture in " + ALBUM_ART_FETCH_DELAY_SECONDS + " seconds... (" + (ALBUM_ART_FETCH_TRIES - tries) + " tries left)");
            }

            try {
                Thread.sleep(ALBUM_ART_FETCH_DELAY_SECONDS * 1000L);
            } catch (InterruptedException e) { }
        } while (tries < ALBUM_ART_FETCH_TRIES);
    }

    public InputStream resizeImage(InputStream input) throws IOException {
        try (input) {
            Image image = ImageIO.read(input);

            BufferedImage resizedImage = new BufferedImage(ALBUM_ART_TEXTURE_SIZE, ALBUM_ART_TEXTURE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = resizedImage.createGraphics();
            graphics.drawImage(image, 0,0, ALBUM_ART_TEXTURE_SIZE, ALBUM_ART_TEXTURE_SIZE, null);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outStream);
            return new ByteArrayInputStream(outStream.toByteArray());
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

    public static SongInfoOverlay getInstance() {
        return instance;
    }
}
