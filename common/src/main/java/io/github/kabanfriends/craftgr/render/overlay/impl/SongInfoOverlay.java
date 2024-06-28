package io.github.kabanfriends.craftgr.render.overlay.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.YACLScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.render.overlay.Overlay;
import io.github.kabanfriends.craftgr.render.widget.impl.ScrollingText;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.song.SongProviderManager;
import io.github.kabanfriends.craftgr.util.*;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;
import org.joml.Vector2f;
import org.joml.Vector2i;

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
    
    private static final int COLOR_WHITE = 0xFFFFFF;

    private static final ResourceLocation ALBUM_ART_PLACEHOLDER_LOCATION = ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "textures/album_placeholder.png");
    private static final ResourceLocation ALBUM_ART_LOCATION = ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "album");

    private static SongInfoOverlay instance;

    private final TextureManager textureManager;
    private final ScrollingText songTitleText;

    private boolean isAlbumArtReady;
    private boolean expanded;
    private boolean muted;

    public SongInfoOverlay(TextureManager textureManager) {
        SongInfoOverlay.instance = this;

        this.textureManager = textureManager;
        this.expanded = false;
        this.songTitleText = new ScrollingText(0, 0, Component.translatable("text.craftgr.song.unknown"));

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

        Font font = CraftGR.MC.font;

        float scale = GRConfig.getValue("overlayScale");

        int albumArtWidth = GRConfig.getValue("hideAlbumArt") ? -ART_LEFT_PADDING : ART_SIZE;

        Vector2f size = getOverlaySize();
        float width = size.x();
        float height = size.y();

        OverlayPosition position = GRConfig.getValue("overlayPosition");
        Vector2i coords = getOverlayCoordinate(position, width, height);
        int x = coords.x();
        int y = coords.y();

        // Rendering
        PoseStack poseStack = graphics.pose();

        RenderUtil.setZLevelPre(poseStack, 400);
        poseStack.scale(RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale));

        RenderUtil.fill(poseStack, x, y, x + width, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (!GRConfig.<Boolean>getValue("hideAlbumArt")) {
            graphics.blit(shouldRenderAlbumArt() ? ALBUM_ART_LOCATION : ALBUM_ART_PLACEHOLDER_LOCATION, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
        }

        poseStack.pushPose();
        poseStack.scale(2, 2, 2);

        Song song = SongProviderManager.getProvider().getCurrentSong();

        if (song != null) {
            if (!song.metadata().intermission()) {
                int dotWidth = font.width("...");

                String year = null;
                if (song.metadata().year() != null) {
                    year = "(" + song.metadata().year() + ")";
                }

                String[] strings = {year, song.metadata().artist(), song.metadata().album(), song.metadata().circle()};
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
                graphics.drawString(CraftGR.MC.font, CraftGR.AUDIO_MUTED_ICON, (x + (int) width - MUTED_ICON_RIGHT_PADDING - MUTED_ICON_SIZE) / 2, (y + MUTED_ICON_TOP_PADDING) / 2, COLOR_WHITE);
            } else if (muted) {
                muted = false;
                updateScrollWidth();
            }
        }

        poseStack.popPose();

        if (song == null || song.metadata().intermission()) {
            RenderUtil.fill(poseStack, x, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
        } else {
            graphics.drawString(CraftGR.MC.font, formatTime(song.getLocalPlayedTime()), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, COLOR_WHITE);

            int timerWidth = font.width(formatTime(song.metadata().duration()));
            graphics.drawString(CraftGR.MC.font, formatTime(song.metadata().duration()), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, COLOR_WHITE);

            RenderUtil.fill(poseStack, x, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + (float) song.getLocalPlayedTime() / song.metadata().duration() * width, y + height, GRConfig.<Color>getValue("overlayBarColor").getRGB() + 0xFF000000, 0.6f);
            RenderUtil.fill(poseStack, x + (float) song.getLocalPlayedTime() / song.metadata().duration() * width, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.<Color>getValue("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
        }

        songTitleText.setX(x + ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH);
        songTitleText.setY(y + INFO_TOP_PADDING);
        songTitleText.render(graphics, mouseX, mouseY);

        RenderUtil.setZLevelPost(poseStack);

        // Mouse hover detection
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

    @Override
    public boolean mouseClick(int mouseX, int mouseY) {
        if (CraftGR.MC.screen instanceof ConfirmLinkScreen) return true;
        if (CraftGR.MC.screen instanceof LevelLoadingScreen) return true;
        if (CraftGR.MC.screen instanceof ReceivingLevelScreen) return true;
        if (CraftGR.MC.screen instanceof ProgressScreen) return true;
        if (CraftGR.MC.screen instanceof ConnectScreen) return true;
        if (CraftGR.MC.screen instanceof GenericMessageScreen) return true;

        if (CraftGR.getPlatform().isInModMenu()) return true;

        if (ModUtil.isConfigModAvailable() && CraftGR.MC.screen instanceof YACLScreen) return true;

        OverlayVisibility visibility = GRConfig.getValue("overlayVisibility");

        if (visibility == OverlayVisibility.NONE) return true;
        if (visibility == OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return true;

        Song song = SongProviderManager.getProvider().getCurrentSong();

        if (song != null && GRConfig.<Boolean>getValue("openAlbum")) {
            if (song.metadata().intermission()) return true;

            float scale = GRConfig.getValue("overlayScale");

            float scaledX = mouseX / RenderUtil.getUIScale(scale);
            float scaledY = mouseY / RenderUtil.getUIScale(scale);

            Vector2f size = getOverlaySize();
            float width = size.x();
            float height = size.y();

            OverlayPosition position = GRConfig.getValue("overlayPosition");
            Vector2i coords = getOverlayCoordinate(position, width, height);
            int x = coords.x();
            int y = coords.y();

            if (scaledX >= x && scaledX <= x + width && scaledY >= y && scaledY <= y + height) {
                String link = "https://gensokyoradio.net/music/album/" + song.metadata().albumId();
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

    public void updateSongTitle() {
        songTitleText.resetScroll();

        Song song = SongProviderManager.getProvider().getCurrentSong();
        if (song == null) {
            songTitleText.setText(Component.translatable("text.craftgr.song.unknown"));
        } else if (song.metadata().intermission()) {
            songTitleText.setText(Component.translatable("text.craftgr.song.intermission"));
        } else {
            songTitleText.setText(Component.literal(song.metadata().title()));
        }
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

    public void updateAlbumArtTexture() {
        isAlbumArtReady = false;

        Song song = SongProviderManager.getProvider().getCurrentSong();
        if (song == null || song.metadata().albumArt() == null || song.metadata().albumArt().isEmpty()) {
            return;
        }

        String url = GRConfig.getValue("urlAlbumArt") + song.metadata().albumArt();
        int tries = 0;

        do {
            tries++;

            try {
                HttpGet get = HttpUtil.get(url);

                try (
                        ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
                        InputStream stream = resizeImage(response.getResponse().getEntity().getContent())
                ) {
                    ThreadLocals.PNG_INFO_BYPASS_VALIDATION.set(true);
                    NativeImage image = NativeImage.read(stream);

                    CraftGR.MC.execute(() -> {
                        textureManager.register(ALBUM_ART_LOCATION, new DynamicTexture(image));
                        isAlbumArtReady = true;
                    });
                }
                break;
            } catch (Exception e) {
                CraftGR.log(Level.ERROR, "Error while creating album art texture! (" + url + ")");
                e.printStackTrace();
                textureManager.release(ALBUM_ART_LOCATION);
            } finally {
                ThreadLocals.PNG_INFO_BYPASS_VALIDATION.remove();
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

    private Vector2i getOverlayCoordinate(OverlayPosition position, float width, float height) {
        float scale = GRConfig.getValue("overlayScale");
        float offset = 10 / scale;
        int x = (int)(CraftGR.MC.getWindow().getWidth() / scale - width - offset);
        int y = (int)(CraftGR.MC.getWindow().getHeight() / scale - height - offset);

        return switch (position) {
            case TOP_RIGHT -> new Vector2i(x, (int) offset);
            case TOP_LEFT -> new Vector2i((int) offset, (int) offset);
            case BOTTOM_RIGHT -> new Vector2i(x, y);
            case BOTTOM_LEFT -> new Vector2i((int) offset, y);
        };
    }

    private Vector2f getOverlaySize() {
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

        return new Vector2f(width, height);
    }

    private int getMaxTextWidth() {
        Song song = SongProviderManager.getProvider().getCurrentSong();
        Font font = CraftGR.MC.font;

        if (song == null || song.metadata().intermission()) {
            return font.width(songTitleText.getText());
        }

        return NumberUtils.max(
                font.width(songTitleText.getText()),
                font.width("(" + song.metadata().year() + ")"),
                font.width(song.metadata().artist()),
                font.width(song.metadata().album()),
                font.width(song.metadata().circle())
        );
    }

    @SuppressWarnings("ConstantConditions")
    private boolean shouldRenderAlbumArt() {
        Song song = SongProviderManager.getProvider().getCurrentSong();
        return song != null &&
                !song.metadata().intermission() &&
                isAlbumArtReady &&
                textureManager.getTexture(ALBUM_ART_LOCATION, null) != null;
    }

    private static String formatTime(long time) {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;

        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static SongInfoOverlay getInstance() {
        return instance;
    }
}
