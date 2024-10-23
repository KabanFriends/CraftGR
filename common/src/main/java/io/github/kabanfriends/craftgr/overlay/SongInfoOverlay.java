package io.github.kabanfriends.craftgr.overlay;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.YACLScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.audio.RadioStream;
import io.github.kabanfriends.craftgr.overlay.widget.impl.ScrollingText;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.*;
import io.github.kabanfriends.craftgr.util.render.RenderUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final CraftGR craftGR;
    private final ScrollingText songTitleText;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private boolean expanded;
    private boolean muted;

    public SongInfoOverlay(CraftGR craftGR) {
        super();

        this.craftGR = craftGR;
        this.songTitleText = new ScrollingText(0, 0, Component.translatable("text.craftgr.song.unknown"));
        this.expanded = false;

        updateScrollWidth();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft minecraft = craftGR.getMinecraft();
        OverlayVisibility visibility = ModConfig.get("overlayVisibility");

        if (minecraft.screen == null) {
            if (visibility != SongInfoOverlay.OverlayVisibility.ALWAYS) return;
        } else {
            if (visibility == SongInfoOverlay.OverlayVisibility.NONE) return;
            if (visibility == SongInfoOverlay.OverlayVisibility.CHAT && !(minecraft.screen instanceof ChatScreen)) return;
        }

        Font font = minecraft.font;

        float scale = ModConfig.get("overlayScale");

        int albumArtWidth = ModConfig.get("hideAlbumArt") ? -ART_LEFT_PADDING : ART_SIZE;

        Vector2f size = getOverlaySize();
        float width = size.x();
        float height = size.y();

        OverlayPosition position = ModConfig.get("overlayPosition");
        Vector2i coords = getOverlayCoordinate(position, width, height);
        int x = coords.x();
        int y = coords.y();

        // Rendering
        PoseStack poseStack = graphics.pose();

        RenderUtil.setZLevelPre(poseStack, 400);
        poseStack.scale(RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale));

        RenderUtil.fill(poseStack, x, y, x + width, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, ModConfig.<Color>get("overlayBgColor").getRGB() + 0xFF000000, 0.6f);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (!ModConfig.<Boolean>get("hideAlbumArt")) {
            graphics.blit(RenderType::guiTextured, shouldRenderAlbumArt() ? ALBUM_ART_LOCATION : ALBUM_ART_PLACEHOLDER_LOCATION, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
        }

        poseStack.pushPose();
        poseStack.scale(2, 2, 2);

        Song song = craftGR.getSongProvider().getCurrentSong();

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
                            if (textWidth > ModConfig.<Integer>get("overlayWidth")) {
                                str = font.plainSubstrByWidth(str, ModConfig.<Integer>get("overlayWidth") - dotWidth);
                                if (!str.equals(strings[i])) {
                                    str += "...";
                                }
                            }
                        }

                        graphics.drawString(minecraft.font, str, (x + ART_LEFT_PADDING + ART_INFO_SPACE_WIDTH + albumArtWidth) / 2, (y + INFO_TOP_PADDING + (i > 0 ? YEAR_ARTIST_SPACE_HEIGHT : 0) + INFO_LINE_HEIGHT * (i + 1)) / 2, Color.LIGHT_GRAY.getRGB());
                    }
                }
            }

            RadioStream.State state = craftGR.getRadioStream().getState();
            if (state == RadioStream.State.STOPPED) {
                if (!muted) {
                    muted = true;
                    updateScrollWidth();
                }
                graphics.drawString(minecraft.font, CraftGR.AUDIO_MUTED_ICON, (x + (int) width - MUTED_ICON_RIGHT_PADDING - MUTED_ICON_SIZE) / 2, (y + MUTED_ICON_TOP_PADDING) / 2, COLOR_WHITE);
            } else if (muted) {
                muted = false;
                updateScrollWidth();
            }
        }

        poseStack.popPose();

        if (song == null || song.metadata().intermission()) {
            RenderUtil.fill(poseStack, x, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, x + width, y + height, ModConfig.<Color>get("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
        } else {
            graphics.drawString(minecraft.font, formatTime(song.getLocalPlayedTime()), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, COLOR_WHITE);

            int timerWidth = font.width(formatTime(song.metadata().duration()));
            graphics.drawString(minecraft.font, formatTime(song.metadata().duration()), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, COLOR_WHITE);

            RenderUtil.fill(poseStack, x, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + (float) song.getLocalPlayedTime() / song.metadata().duration() * width, y + height, ModConfig.<Color>get("overlayBarColor").getRGB() + 0xFF000000, 0.6f);
            RenderUtil.fill(poseStack, x + (float) song.getLocalPlayedTime() / song.metadata().duration() * width, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + width, y + height, ModConfig.<Color>get("overlayBgColor").getRGB() + 0xFF000000, 0.6f);
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
        Minecraft minecraft = craftGR.getMinecraft();

        if (minecraft.screen instanceof ConfirmLinkScreen) return true;
        if (minecraft.screen instanceof LevelLoadingScreen) return true;
        if (minecraft.screen instanceof ReceivingLevelScreen) return true;
        if (minecraft.screen instanceof ProgressScreen) return true;
        if (minecraft.screen instanceof ConnectScreen) return true;
        if (minecraft.screen instanceof GenericMessageScreen) return true;

        if (craftGR.getPlatform().isInModMenu()) return true;

        if (ModUtil.isConfigModAvailable() && minecraft.screen instanceof YACLScreen) return true;

        OverlayVisibility visibility = ModConfig.get("overlayVisibility");

        if (visibility == OverlayVisibility.NONE) return true;
        if (visibility == OverlayVisibility.CHAT && !(minecraft.screen instanceof ChatScreen)) return true;

        Song song = craftGR.getSongProvider().getCurrentSong();

        if (song != null && ModConfig.<Boolean>get("openAlbum")) {
            if (song.metadata().intermission()) return true;

            float scale = ModConfig.get("overlayScale");

            float scaledX = mouseX / RenderUtil.getUIScale(scale);
            float scaledY = mouseY / RenderUtil.getUIScale(scale);

            Vector2f size = getOverlaySize();
            float width = size.x();
            float height = size.y();

            OverlayPosition position = ModConfig.get("overlayPosition");
            Vector2i coords = getOverlayCoordinate(position, width, height);
            int x = coords.x();
            int y = coords.y();

            if (scaledX >= x && scaledX <= x + width && scaledY >= y && scaledY <= y + height) {
                String link = "https://gensokyoradio.net/music/album/" + song.metadata().albumId();
                Screen oldScreen = minecraft.screen;

                minecraft.setScreen(new ConfirmLinkScreen((result) -> {
                    if (result) Util.getPlatform().openUri(link);
                    minecraft.setScreen(oldScreen);
                }, link, true));

                return false;
            }
        }
        return true;
    }

    public void updateScrollWidth() {
        int width = ModConfig.get("overlayWidth");
        if (expanded) {
            width = getMaxTextWidth();

            if (ModConfig.<Integer>get("overlayWidth") > width) {
                width = ModConfig.get("overlayWidth");
            }
        }
        if (muted) {
            width -= (MUTED_ICON_SIZE + TITLE_MUTED_ICON_SPACE) / 2;
        }
        songTitleText.setWidth(width);
        songTitleText.resetScroll();
    }

    public void onSongChanged() {
        updateSongTitle();
        CraftGR.getInstance().getThreadExecutor().submit(() -> downloadAlbumArtTexture());
    }

    private Vector2i getOverlayCoordinate(OverlayPosition position, float width, float height) {
        float scale = ModConfig.get("overlayScale");
        float offset = 10 / scale;
        int x = (int)(craftGR.getMinecraft().getWindow().getWidth() / scale - width - offset);
        int y = (int)(craftGR.getMinecraft().getWindow().getHeight() / scale - height - offset);

        return switch (position) {
            case TOP_RIGHT -> new Vector2i(x, (int) offset);
            case TOP_LEFT -> new Vector2i((int) offset, (int) offset);
            case BOTTOM_RIGHT -> new Vector2i(x, y);
            case BOTTOM_LEFT -> new Vector2i((int) offset, y);
        };
    }

    private Vector2f getOverlaySize() {
        int albumArtWidth;
        if (ModConfig.get("hideAlbumArt")) albumArtWidth = -ART_LEFT_PADDING;
        else albumArtWidth = ART_SIZE;

        float width;
        float height = ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING + PROGRESS_BAR_HEIGHT;

        if (expanded) {
            int maxWidth = getMaxTextWidth();

            if (ModConfig.<Integer>get("overlayWidth") > maxWidth) {
                maxWidth = ModConfig.get("overlayWidth");
            }

            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + maxWidth * 2 + INFO_RIGHT_PADDING;
        } else {
            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + ModConfig.<Integer>get("overlayWidth") * 2 + INFO_RIGHT_PADDING;
        }

        return new Vector2f(width, height);
    }

    private int getMaxTextWidth() {
        Song song = craftGR.getSongProvider().getCurrentSong();
        Font font = craftGR.getMinecraft().font;

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

    private void updateSongTitle() {
        songTitleText.resetScroll();

        Song song = craftGR.getSongProvider().getCurrentSong();
        if (song == null) {
            songTitleText.setText(Component.translatable("text.craftgr.song.unknown"));
        } else if (song.metadata().intermission()) {
            songTitleText.setText(Component.translatable("text.craftgr.song.intermission"));
        } else {
            songTitleText.setText(Component.literal(song.metadata().title()));
        }
    }

    private void downloadAlbumArtTexture() {
        downloadAlbumArtTexture(0);
    }

    private void downloadAlbumArtTexture(int attempt) {
        Song song = craftGR.getSongProvider().getCurrentSong();
        if (song == null || song.metadata().albumArt() == null || song.metadata().albumArt().isEmpty()) {
            return;
        }

        TextureManager textureManager = craftGR.getMinecraft().getTextureManager();
        textureManager.release(ALBUM_ART_LOCATION);

        try {
            HttpGet get = HttpUtil.get(song.metadata().albumArt());
            try (
                    CloseableHttpResponse response = craftGR.getHttpClient().execute(get);
                    InputStream stream = resizeImage(response.getEntity().getContent())
            ) {
                ThreadLocals.PNG_INFO_BYPASS_VALIDATION.set(true);
                NativeImage image = NativeImage.read(stream);
                craftGR.getMinecraft().execute(() -> textureManager.register(ALBUM_ART_LOCATION, new DynamicTexture(image)));
            }
        } catch (Exception e) {
            craftGR.log(Level.ERROR, "Error while creating album art texture (" + song.metadata().albumArt() + ")" + ( attempt < ALBUM_ART_FETCH_TRIES ? ", retrying" : "") + ": " + ExceptionUtil.getStackTrace(e));
            textureManager.release(ALBUM_ART_LOCATION);

            if (attempt < ALBUM_ART_FETCH_TRIES) {
                scheduler.schedule(() -> downloadAlbumArtTexture(attempt + 1), ALBUM_ART_FETCH_DELAY_SECONDS, TimeUnit.SECONDS);
            }
        } finally {
            ThreadLocals.PNG_INFO_BYPASS_VALIDATION.remove();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean shouldRenderAlbumArt() {
        Song song = craftGR.getSongProvider().getCurrentSong();
        return song != null &&
                !song.metadata().intermission() &&
                craftGR.getMinecraft().getTextureManager().getTexture(ALBUM_ART_LOCATION, null) != null;
    }

    private static String formatTime(long time) {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;

        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    private static InputStream resizeImage(InputStream input) throws IOException {
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
}
