package io.github.kabanfriends.craftgr.render.overlay.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.render.overlay.Overlay;
import io.github.kabanfriends.craftgr.render.widget.impl.ScrollingText;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.HttpUtil;
import io.github.kabanfriends.craftgr.util.RenderUtil;
import io.github.kabanfriends.craftgr.util.ResponseHolder;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.InputStream;

public class SongInfoOverlay extends Overlay {

    //<editor-fold desc="UI size constants">
    private static final int ART_TOP_PADDING = 6;
    private static final int ART_BOTTOM_PADDING = 14;
    private static final int ART_LEFT_PADDING = 6;

    private static final int ART_INFO_SPACE_WIDTH = 12;

    private static final int INFO_TOP_PADDING = 8;
    private static final int INFO_RIGHT_PADDING = 6;
    private static final int INFO_LINE_HEIGHT = 20;
    private static final int YEAR_ARTIST_SPACE_HEIGHT = 7;

    private static final int TIMER_RIGHT_PADDING = 6;
    private static final int ART_TIMER_SPACE_HEIGHT = 4;

    private static final int PROGRESS_BAR_HEIGHT = 6;

    private static final int ART_SIZE = 105;
    //</editor-fold>

    private static final ResourceLocation ALBUM_ART_PLACEHOLDER = new ResourceLocation(CraftGR.MOD_ID, "textures/album_placeholder.png");

    private static SongInfoOverlay INSTANCE;

    private ResourceLocation albumArtTexture;
    private ScrollingText songTitleText;
    private boolean expanded;

    public SongInfoOverlay() {
        INSTANCE = this;

        expanded = false;
        songTitleText = new ScrollingText(0, 0, Component.empty());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY) {
        OverlayVisibility visibility = GRConfig.getConfig().overlayVisibility;

        if (CraftGR.MC.screen == null) {
            if (visibility != SongInfoOverlay.OverlayVisibility.ALWAYS) return;
        }else {
            if (visibility == SongInfoOverlay.OverlayVisibility.NONE) return;
            if (visibility == SongInfoOverlay.OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return;
        }

        Song currentSong = SongHandler.getInstance().getCurrentSong();

        if (currentSong != null) {
            Font font = CraftGR.MC.font;

            float scale = GRConfig.getConfig().overlayScale;

            int albumArtWidth;
            if (GRConfig.getConfig().hideAlbumArt) albumArtWidth = -ART_LEFT_PADDING;
            else albumArtWidth = ART_SIZE;

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getConfig().overlayPosition, width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            //Rendering
            RenderUtil.setZLevelPre(poseStack, 400);
            poseStack.scale(RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale), RenderUtil.getUIScale(scale));

            RenderUtil.fill(poseStack, x, y, x + width, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);

            if (!GRConfig.getConfig().hideAlbumArt) {
                if (albumArtTexture == null) {
                    RenderUtil.bindTexture(ALBUM_ART_PLACEHOLDER);
                } else {
                    RenderUtil.bindTexture(albumArtTexture);
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, x + ART_LEFT_PADDING, y + ART_TOP_PADDING, 0f, 0f, ART_SIZE, ART_SIZE, ART_SIZE, ART_SIZE);
            }

            poseStack.pushPose();
            poseStack.scale(2, 2, 2);

            if (!currentSong.isIntermission()) {
                int dotWidth = font.width("...");

                String[] strings = {"(" + currentSong.year + ")", currentSong.artist, currentSong.album, currentSong.circle};
                for (int i = 0; i < 4; i++) {
                    String str = strings[i];
                    if (str != null) {
                        if (!expanded) {
                            str = font.plainSubstrByWidth(str, GRConfig.getConfig().overlayWidth - dotWidth);
                            if (!str.equals(strings[i])) {
                                str += "...";
                            }
                        }

                        GuiComponent.drawString(poseStack, CraftGR.MC.font, str, (x + ART_LEFT_PADDING + ART_INFO_SPACE_WIDTH + albumArtWidth) / 2, (y + INFO_TOP_PADDING + (i > 0 ? YEAR_ARTIST_SPACE_HEIGHT : 0) + INFO_LINE_HEIGHT * (i + 1)) / 2, Color.LIGHT_GRAY.getRGB());
                    }
                }
            }

            poseStack.popPose();

            if (currentSong.isIntermission()) {
                RenderUtil.fill(poseStack, x, y + ART_SIZE + ART_TOP_PADDING + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            } else {
                long duration = currentSong.songEnd - currentSong.songStart;
                long played = System.currentTimeMillis() / 1000L - SongHandler.getInstance().getSongStart();
                if (played > duration) played = duration;

                GuiComponent.drawString(poseStack, CraftGR.MC.font, getTimer((int) played), x + ART_LEFT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, Color.WHITE.getRGB());

                int timerWidth = font.width(getTimer((int) duration));
                GuiComponent.drawString(poseStack, CraftGR.MC.font, getTimer((int) duration), x + (int) width - timerWidth - TIMER_RIGHT_PADDING, y + ART_TOP_PADDING + ART_SIZE + ART_TIMER_SPACE_HEIGHT, Color.WHITE.getRGB());

                RenderUtil.fill(poseStack, x, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + (float) played / duration * width, y + height, GRConfig.getConfig().overlayBarColor + 0xFF000000, 0.6f);
                RenderUtil.fill(poseStack, x + (float) played / duration * width, y + ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            }

            songTitleText.setX(x + ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH);
            songTitleText.setY(y + INFO_TOP_PADDING);

            if (!currentSong.isIntermission() && currentSong.title != null) {
                songTitleText.render(poseStack, mouseX, mouseY);
            }

            RenderUtil.setZLevelPost(poseStack);

            //Mouse hover detection
            float mouseScaledX = mouseX / RenderUtil.getUIScale(scale);
            float mouseScaledY = mouseY / RenderUtil.getUIScale(scale);

            if (mouseScaledX >= x && mouseScaledX <= x + width && mouseScaledY >= y && mouseScaledY <= y + height) {
                if (!expanded) {
                    songTitleText.resetScroll();
                    expanded = true;
                }
                songTitleText.setWidth(getMaxTextWidth());
            } else {
                if (expanded) {
                    songTitleText.resetScroll();
                    expanded = false;
                }
                songTitleText.setWidth(GRConfig.getConfig().overlayWidth);
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

        if (CraftGR.getPlatform().isModLoaded("cloth-config2") || CraftGR.getPlatform().isModLoaded("cloth-config")) {
            if (CraftGR.MC.screen instanceof ConfigScreen) return true;
        }

        OverlayVisibility visibility = GRConfig.getConfig().overlayVisibility;

        if (visibility == OverlayVisibility.NONE) return true;
        if (visibility == OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return true;

        Song currentSong = SongHandler.getInstance().getCurrentSong();

        if (currentSong != null && GRConfig.getConfig().openAlbum) {
            if (currentSong.isIntermission()) return true;

            float scale = GRConfig.getConfig().overlayScale;

            float scaledX = mouseX / RenderUtil.getUIScale(scale);
            float scaledY = mouseY / RenderUtil.getUIScale(scale);

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getConfig().overlayPosition, width, height);
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
        Song currentSong = SongHandler.getInstance().getCurrentSong();

        int albumArtWidth;
        if (GRConfig.getConfig().hideAlbumArt) albumArtWidth = -ART_LEFT_PADDING;
        else albumArtWidth = ART_SIZE;

        float width;
        float height = ART_TOP_PADDING + ART_SIZE + ART_BOTTOM_PADDING + PROGRESS_BAR_HEIGHT;

        if (expanded) {
            int maxWidth = getMaxTextWidth();

            if (GRConfig.getConfig().overlayWidth > maxWidth) {
                maxWidth = GRConfig.getConfig().overlayWidth;
            }

            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + maxWidth * 2 + INFO_RIGHT_PADDING;
        } else {
            width = ART_LEFT_PADDING + albumArtWidth + ART_INFO_SPACE_WIDTH + GRConfig.getConfig().overlayWidth * 2 + INFO_RIGHT_PADDING;
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

    public void createAlbumArtTexture(Song song) {
        albumArtTexture = null;
        String url = GRConfig.getConfig().url.albumArtURL + song.albumArt;

        CraftGR.EXECUTOR.submit(() -> {
            try {
                HttpGet get = HttpUtil.get(url);
                ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
                InputStream stream = response.getResponse().getEntity().getContent();
                DynamicTexture texture = new DynamicTexture(NativeImage.read(stream));
                response.close();

                //Wait for texture manager to be initialized
                while (CraftGR.MC.getTextureManager() == null) {
                    Thread.sleep(1);
                }

                albumArtTexture = CraftGR.MC.getTextureManager().register("craftgr_album", texture);
            } catch (Exception e) {
                CraftGR.log(Level.ERROR, "Error while creating album art texture!");
                e.printStackTrace();
            }
        });
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
        return INSTANCE;
    }
}
