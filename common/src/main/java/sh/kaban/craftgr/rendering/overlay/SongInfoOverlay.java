package sh.kaban.craftgr.rendering.overlay;

import com.mojang.blaze3d.platform.NativeImage;
//? if > 1.21.1
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.gui.YACLScreen;
import sh.kaban.craftgr.CraftGR;
import sh.kaban.craftgr.audio.FreqRenderer;
import sh.kaban.craftgr.config.ModConfig;
import sh.kaban.craftgr.audio.Radio;
import sh.kaban.craftgr.rendering.element.ScrollingText;
import sh.kaban.craftgr.song.Song;
import sh.kaban.craftgr.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if > 1.21.11 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else
//import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
//? if > 1.21.1
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
//? if > 1.21.1
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Vector2i;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SongInfoOverlay extends Overlay {

    private static final Logger LOGGER = Logs.logger();

    //<editor-fold desc="UI size constants">
    public static final int ART_TOP_PADDING = 6;
    public static final int ART_BOTTOM_PADDING = 14;
    public static final int ART_LEFT_PADDING = 6;
    public static final int ART_INFO_SPACE_WIDTH = 12;
    public static final int ART_SIZE = 106;

    public static final int INFO_TOP_PADDING = 8;
    public static final int INFO_RIGHT_PADDING = 6;
    public static final int INFO_LINE_HEIGHT = 20;
    public static final int YEAR_ARTIST_SPACE_HEIGHT = 8;

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

    //? if > 1.21.1 {
    private static final Identifier HIGHLIGHTED_BORDER_SPRITE = IdentifierUtil.thisMod("highlighted_border");

    private static final Identifier ALBUM_ART_PLACEHOLDER_LOCATION = IdentifierUtil.thisMod("textures/album_placeholder.png");
    private static final Identifier ALBUM_ART_LOCATION = IdentifierUtil.thisMod("album");
    //? } else {
    /*private static final net.minecraft.resources.ResourceLocation HIGHLIGHTED_BORDER_SPRITE = IdentifierUtil.thisMod("highlighted_border");

    private static final net.minecraft.resources.ResourceLocation ALBUM_ART_PLACEHOLDER_LOCATION = IdentifierUtil.thisMod("textures/album_placeholder.png");
    private static final net.minecraft.resources.ResourceLocation ALBUM_ART_LOCATION = IdentifierUtil.thisMod("album");
    *///? }

    private final CraftGR craftGR;
    private final ScrollingText songTitleText;

    private final ScheduledExecutorService scheduler = Schedulers.newDaemonScheduler("album-art");

    private boolean albumArtLoaded;
    private boolean expanded;
    private boolean muted;

    public SongInfoOverlay(CraftGR craftGR) {
        super();

        this.craftGR = craftGR;
        this.songTitleText = new ScrollingText(0, 0, Component.translatable("text.craftgr.song.unknown"));
        this.expanded = false;
        this.albumArtLoaded = false;

        updateScrollWidth();
    }

    @Override
    //? if > 1.21.11 {
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    //? } else
    //public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;

        float scale = ModConfig.get("overlayScale");

        int albumArtWidth = ModConfig.get("hideAlbumArt") ? -ART_LEFT_PADDING : ART_SIZE;

        // Mouse hover detection
        if (isHovered(mouseX, mouseY)) {
            if (!expanded) {
                expanded = true;
                updateScrollWidth();
            }
        } else if (expanded) {
            expanded = false;
            updateScrollWidth();
        }

        // Determine overlay size and position
        Vector2i size = getOverlaySize();
        int width = size.x();
        int height = size.y();

        OverlayPosition position = ModConfig.get("overlayPosition");
        Vector2i coords = getOverlayCoordinate(position, width, height);
        int x = coords.x();
        int y = coords.y();

        // Rendering
        var pose = graphics.pose();

        //? if <= 1.21.1
        //RenderUtil.setZLevelPre(pose, 400);

        float renderScale = RenderUtil.getUIScale(scale);
        //? if > 1.21.1 {
        pose.pushMatrix();
        pose.scale(renderScale, renderScale);
        //? } else {
        /*pose.pushPose();
        pose.scale(renderScale, renderScale, renderScale);
        *///? }
        graphics.fill(x, y, x + width, y + height, ModConfig.<Color>get("overlayBgColor").getRGB() + 0x99000000);

        boolean showVisualizer = ModConfig.<Boolean>get("showVisualizer") &&
                craftGR.getRadio().getState() == Radio.State.PLAYING &&
                craftGR.getRadio().getAudioPlayer().getFreqRenderer() != null;

        if (showVisualizer) {
            FreqRenderer freq = craftGR.getRadio().getAudioPlayer().getFreqRenderer();

            double[] bands = freq.calculateBandsNow();
            float barWidth = (float) width / bands.length;

            for (int i = 0; i < bands.length; i++) {
                float barX = x + (bands.length - i - 1) * barWidth;
                float barY = y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING;

                graphics.fill((int) barX, (int) barY, (int) (barX + barWidth), (int) (barY - (float) bands[i] * (ART_SIZE + ART_BOTTOM_PADDING)), 0x40000000);
            }
        }

        if (!ModConfig.<Boolean>get("hideAlbumArt")) {
            //? if > 1.21.1 {
            graphics.blit(RenderPipelines.GUI_TEXTURED, shouldRenderAlbumArt() ? ALBUM_ART_LOCATION : ALBUM_ART_PLACEHOLDER_LOCATION, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
            //? } else
            //graphics.blit(shouldRenderAlbumArt() ? ALBUM_ART_LOCATION : ALBUM_ART_PLACEHOLDER_LOCATION, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
        }

        if (isClickable() && isHovered(mouseX, mouseY)) {
            //? if > 1.21.1 {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHLIGHTED_BORDER_SPRITE, x - 2, y - 2, width + 4, height + 4);
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            //? } else
            //graphics.blitSprite(HIGHLIGHTED_BORDER_SPRITE, x - 2, y - 2, width + 4, height + 4);
        }

        //? if > 1.21.1 {
        pose.pushMatrix();
        pose.scale(2, 2);
        //? } else {
        /*pose.pushPose();
        pose.scale(2f, 2f, 2f);
        *///? }

        Song song = craftGR.getSongProvider().getCurrentSong();

        if (song != null) {
            if (!song.metadata().intermission()) {
                int dotWidth = font.width("...");

                String year = song.metadata().year() == null ? null : "(" + song.metadata().year() + ")";

                // Possibly in the future these should be moved into separate widgets...
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

                        //? if > 1.21.11 {
                        graphics.text(Minecraft.getInstance().font, str, (x + ART_LEFT_PADDING + ART_INFO_SPACE_WIDTH + albumArtWidth) / 2, (y + INFO_TOP_PADDING + (i > 0 ? YEAR_ARTIST_SPACE_HEIGHT : 0) + INFO_LINE_HEIGHT * (i + 1)) / 2, Color.LIGHT_GRAY.getRGB());
                        //? } else
                        //graphics.drawString(Minecraft.getInstance().font, str, (x + ART_LEFT_PADDING + ART_INFO_SPACE_WIDTH + albumArtWidth) / 2, (y + INFO_TOP_PADDING + (i > 0 ? YEAR_ARTIST_SPACE_HEIGHT : 0) + INFO_LINE_HEIGHT * (i + 1)) / 2, Color.LIGHT_GRAY.getRGB());
                    }
                }
            }

            Radio.State state = craftGR.getRadio().getState();
            if (state == Radio.State.STOPPED) {
                if (!muted) {
                    muted = true;
                    updateScrollWidth();
                }
                //? if > 1.21.11 {
                graphics.text(Minecraft.getInstance().font, ComponentUtil.AUDIO_MUTED_ICON, (x + (int) width - MUTED_ICON_RIGHT_PADDING - MUTED_ICON_SIZE) / 2, (y + MUTED_ICON_TOP_PADDING) / 2, 0xFFFFFFFF);
                //? } else
                //graphics.drawString(Minecraft.getInstance().font, ComponentUtil.AUDIO_MUTED_ICON, (x + (int) width - MUTED_ICON_RIGHT_PADDING - MUTED_ICON_SIZE) / 2, (y + MUTED_ICON_TOP_PADDING) / 2, 0xFFFFFFFF);
            } else if (muted) {
                muted = false;
                updateScrollWidth();
            }
        }

        //? if > 1.21.1 {
        pose.popMatrix();
        //? } else
        //pose.popPose();

        if (song != null && !song.metadata().intermission()) {
            long durationMillis = song.metadata().duration() * 1000L;
            graphics.fill(x, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, (int) (x + (float) song.getLocalPlayedTime() / durationMillis * width), y + height, 0x40FFFFFF);
            if (showVisualizer) {
                graphics.fill((int) (x + (float) song.getLocalPlayedTime() / durationMillis * width), y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + width, y + height, 0x40000000);
            }

            //? if > 1.21.11 {
            graphics.text(Minecraft.getInstance().font, formatTime(song.getLocalPlayedTime() / 1000L), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, 0xFFFFFFFF);
            //? } else
            //graphics.drawString(Minecraft.getInstance().font, formatTime(song.getLocalPlayedTime() / 1000L), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, 0xFFFFFFFF);

            int timerWidth = font.width(formatTime(song.metadata().duration()));
            //? if > 1.21.11 {
            graphics.text(Minecraft.getInstance().font, formatTime(song.metadata().duration()), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, 0xFFFFFFFF);
            //? } else
            //graphics.drawString(Minecraft.getInstance().font, formatTime(song.metadata().duration()), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, 0xFFFFFFFF);
        } else if (showVisualizer) {
            graphics.fill(x, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, x + width, y + height, 0x40000000);
        }

        songTitleText.setX(x + ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH);
        songTitleText.setY(y + INFO_TOP_PADDING);
        songTitleText.render(graphics, mouseX, mouseY);

        //? if > 1.21.1 {
        pose.popMatrix();
        //? } else
        //pose.popPose();

        //? if <= 1.21.1
        //RenderUtil.setZLevelPost(pose);
    }

    @Override
    public boolean mouseClick(int mouseX, int mouseY) {
        if (!isClickable()) return true;

        Song song = craftGR.getSongProvider().getCurrentSong();
        if (song != null && ModConfig.<Boolean>get("openAlbum")) {
            if (isHovered(mouseX, mouseY)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                String link = "https://gensokyoradio.net/music/album/" + song.metadata().albumId();

                ConfirmLinkScreen.confirmLinkNow(
                        //? if > 1.21.11 {
                        Minecraft.getInstance().gui.screen(),
                        //? } else
                        //Minecraft.getInstance().screen,
                        URI.create(link)
                );

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

    public boolean shouldRender() {
        if (!isActive()) return false;
        //? if > 1.21.11 {
        if (Minecraft.getInstance().gui.hud.isHidden()) return false;
        //? } else
        //if (Minecraft.getInstance().options.hideGui) return false;
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) return false;

        OverlayVisibility visibility = ModConfig.get("overlayVisibility");

        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            if (visibility != OverlayVisibility.ALWAYS) return false;
        } else {
            if (visibility == OverlayVisibility.NONE) return false;
            if (visibility == OverlayVisibility.CHAT && !(screen instanceof ChatScreen)) return false;
        }

        return true;
    }

    private Vector2i getOverlayCoordinate(OverlayPosition position, float width, float height) {
        float scale = ModConfig.get("overlayScale");
        float offset = 10 / scale;
        int x = (int)(Minecraft.getInstance().getWindow().getWidth() / scale - width - offset);
        int y = (int)(Minecraft.getInstance().getWindow().getHeight() / scale - height - offset);

        return switch (position) {
            case TOP_RIGHT -> new Vector2i(x, (int) offset);
            case TOP_LEFT -> new Vector2i((int) offset, (int) offset);
            case BOTTOM_RIGHT -> new Vector2i(x, y);
            case BOTTOM_LEFT -> new Vector2i((int) offset, y);
        };
    }

    private Vector2i getOverlaySize() {
        int albumArtWidth;
        if (ModConfig.get("hideAlbumArt")) albumArtWidth = -ART_LEFT_PADDING;
        else albumArtWidth = ART_SIZE;

        int width;
        int height = ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING + PROGRESS_BAR_HEIGHT;

        if (expanded) {
            int maxWidth = getMaxTextWidth();

            if (ModConfig.<Integer>get("overlayWidth") > maxWidth) {
                maxWidth = ModConfig.get("overlayWidth");
            }

            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + maxWidth * 2 + INFO_RIGHT_PADDING;
        } else {
            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + ModConfig.<Integer>get("overlayWidth") * 2 + INFO_RIGHT_PADDING;
        }

        return new Vector2i(width, height);
    }

    private int getMaxTextWidth() {
        Song song = craftGR.getSongProvider().getCurrentSong();
        Font font = Minecraft.getInstance().font;

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

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        Minecraft.getInstance().executeBlocking(() -> textureManager.release(ALBUM_ART_LOCATION));
        albumArtLoaded = false;

        try {
            Http.fetch(Http.standardRequest()
                    .uri(URI.create(song.metadata().albumArt()))
                    .build(), HttpResponse.BodyHandlers.ofInputStream())
                    .thenAccept(response -> {
                        try {
                            NativeImage image = NativeImage.read(resizeImage(response.body()));
                            Minecraft.getInstance().executeBlocking(() -> {
                                //? if > 1.21.1 {
                                DynamicTexture texture = new DynamicTexture(null, image);
                                //? } else
                                //DynamicTexture texture = new DynamicTexture(image);

                                textureManager.register(ALBUM_ART_LOCATION, texture);
                                albumArtLoaded = true;
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .join();
        } catch (Exception e) {
            LOGGER.error("Error while creating album art texture from {}{}", song.metadata().albumArt(), attempt < ALBUM_ART_FETCH_TRIES ? ", retrying" : "", e);
            Minecraft.getInstance().executeBlocking(() -> textureManager.release(ALBUM_ART_LOCATION));

            if (attempt < ALBUM_ART_FETCH_TRIES) {
                scheduler.schedule(() -> downloadAlbumArtTexture(attempt + 1), ALBUM_ART_FETCH_DELAY_SECONDS, TimeUnit.SECONDS);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean shouldRenderAlbumArt() {
        Song song = craftGR.getSongProvider().getCurrentSong();
        return song != null &&
                !song.metadata().intermission() &&
                albumArtLoaded;
    }

    private boolean isHovered(int mouseX, int mouseY) {
        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        if (screen == null) return false;
        float scale = ModConfig.get("overlayScale");

        float scaledX = mouseX / RenderUtil.getUIScale(scale);
        float scaledY = mouseY / RenderUtil.getUIScale(scale);

        Vector2i size = getOverlaySize();
        int width = size.x();
        int height = size.y();

        OverlayPosition position = ModConfig.get("overlayPosition");
        Vector2i coords = getOverlayCoordinate(position, width, height);
        int x = coords.x();
        int y = coords.y();

        return scaledX >= x && scaledX <= x + width && scaledY >= y && scaledY <= y + height;
    }

    private boolean isClickable() {
        if (!shouldRender()) return false;

        if (!ModConfig.<Boolean>get("openAlbum")) return false;

        //? if > 1.21.11 {
        Screen screen = Minecraft.getInstance().gui.screen();
        //? } else
        //Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof ConfirmLinkScreen) return false;

        if (craftGR.getPlatformAdapter().isInModMenu()) return false;
        if (ModUtil.isConfigModAvailable() && screen instanceof YACLScreen) return false;

        //? if > 1.21.1 {
        if (Minecraft.getInstance().hasShiftDown()) return false;
        //? } else
        //if (Screen.hasShiftDown()) return false;

        Song song = craftGR.getSongProvider().getCurrentSong();
        if (song == null) return false;
        if (song.metadata().intermission()) return false;

        return true;
    }

    private static String formatTime(long time) {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;

        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    private static InputStream resizeImage(InputStream input) throws IOException {
        try (input) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IOException("Unrecognized image format");
            }

            float scale = Math.min(
                    (float) ALBUM_ART_TEXTURE_SIZE / image.getWidth(),
                    (float) ALBUM_ART_TEXTURE_SIZE / image.getHeight()
            );
            int width = Math.max(1, Math.round(image.getWidth() * scale));
            int height = Math.max(1, Math.round(image.getHeight() * scale));

            BufferedImage texture = new BufferedImage(ALBUM_ART_TEXTURE_SIZE, ALBUM_ART_TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = texture.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, (ALBUM_ART_TEXTURE_SIZE - width) / 2, (ALBUM_ART_TEXTURE_SIZE - height) / 2, width, height, null);
            graphics.dispose();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ImageIO.write(texture, "png", outStream);
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
