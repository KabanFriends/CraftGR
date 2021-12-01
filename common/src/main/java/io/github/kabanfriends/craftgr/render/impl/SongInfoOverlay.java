package io.github.kabanfriends.craftgr.render.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.terraformersmc.modmenu.gui.ModsScreen;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.render.Overlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.RenderUtil;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SongInfoOverlay extends Overlay {

    private static final float BASE_SCALE = 1.0f;
    private static final int ALBUM_ART_SIZE = 105;
    private static final ResourceLocation ALBUM_ART_PLACEHOLDER = new ResourceLocation(CraftGR.MOD_ID, "textures/album_placeholder.png");

    private static SongInfoOverlay INSTANCE;

    private Map<String, ResourceLocation> albumArts;

    public SongInfoOverlay() {
        INSTANCE = this;

        this.albumArts = new HashMap<>();
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

        Song currentSong = SongHandler.getInstance().song;

        if (currentSong != null) {
            Font font = CraftGR.MC.font;

            float scale = GRConfig.getConfig().overlayScale;

            RenderUtil.setZLevelPre(poseStack, 400);
            poseStack.scale(getUIScale(scale), getUIScale(scale), getUIScale(scale));

            int albumArtWidth;
            if (GRConfig.getConfig().hideAlbumArt) albumArtWidth = -6;
            else albumArtWidth = ALBUM_ART_SIZE;

            float[] size = getOverlaySize();
            float width = size[0];
            float height = size[1];

            float[] coord = getOverlayCoordinate(GRConfig.getConfig().overlayPosition, width, height);
            int x = (int) coord[0];
            int y = (int) coord[1];

            RenderUtil.fill(poseStack, x, y, x + width, y + ALBUM_ART_SIZE + 10 + 10, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);

            if (albumArts.containsKey(currentSong.albumArt) && !GRConfig.getConfig().hideAlbumArt) {
                ResourceLocation albumArt = albumArts.get(currentSong.albumArt);

                if (albumArt == null) {
                    RenderUtil.bindTexture(ALBUM_ART_PLACEHOLDER);
                } else {
                    RenderUtil.bindTexture(albumArt);
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, x + 6, y + 6, 0f, 0f, ALBUM_ART_SIZE, ALBUM_ART_SIZE, ALBUM_ART_SIZE, ALBUM_ART_SIZE);
            }

            poseStack.pushPose();
            poseStack.scale(2, 2, 2);

            if (currentSong.intermission) {
                GuiComponent.drawString(poseStack, CraftGR.MC.font, new TranslatableComponent("text.craftgr.song.intermission"), (x + 12 + 8 + albumArtWidth) / 2, (y + 8) / 2, Color.WHITE.getRGB());
            } else {
                GuiComponent.drawString(poseStack, CraftGR.MC.font, currentSong.title, (x + 12 + 8 + albumArtWidth) / 2, (y + 8) / 2, Color.WHITE.getRGB());
                GuiComponent.drawString(poseStack, CraftGR.MC.font, "(" + currentSong.year + ")", (x + 12 + 10 + albumArtWidth) / 2, (y + 8 + 20) / 2, Color.LIGHT_GRAY.getRGB());
                GuiComponent.drawString(poseStack, CraftGR.MC.font, currentSong.artist, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 2) / 2, Color.LIGHT_GRAY.getRGB());
                GuiComponent.drawString(poseStack, CraftGR.MC.font, currentSong.album, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 3) / 2, Color.LIGHT_GRAY.getRGB());
                GuiComponent.drawString(poseStack, CraftGR.MC.font, currentSong.circle, (x + 12 + 8 + albumArtWidth) / 2, (y + 8 + 7 + 20 * 4) / 2, Color.LIGHT_GRAY.getRGB());
            }

            poseStack.popPose();

            if (currentSong.intermission) {
                RenderUtil.fill(poseStack, x, y + ALBUM_ART_SIZE + 10 + 10, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            } else {
                long duration = currentSong.songEnd - currentSong.songStart;
                long played = System.currentTimeMillis() / 1000L - SongHandler.getInstance().songStart;
                if (played > duration) played = duration;

                GuiComponent.drawString(poseStack, CraftGR.MC.font, getTimer((int) played), x + 6, y + ALBUM_ART_SIZE + 10, Color.WHITE.getRGB());

                int timerWidth = font.width(getTimer((int) duration));
                GuiComponent.drawString(poseStack, CraftGR.MC.font, getTimer((int) duration), x + (int) width - timerWidth - 6, y + ALBUM_ART_SIZE + 10, Color.WHITE.getRGB());

                RenderUtil.fill(poseStack, x, y + ALBUM_ART_SIZE + 10 + 10, x + (float) played / duration * width, y + height, GRConfig.getConfig().overlayBarColor + 0xFF000000, 0.6f);
                RenderUtil.fill(poseStack, x + (float) played / duration * width, y + ALBUM_ART_SIZE + 10 + 10, x + width, y + height, GRConfig.getConfig().overlayBgColor + 0xFF000000, 0.6f);
            }

            RenderUtil.setZLevelPost(poseStack);
        }
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY) {
        if (CraftGR.MC.screen instanceof ConfirmLinkScreen) return true;
        else if (CraftGR.MC.screen instanceof LevelLoadingScreen) return true;
        else if (CraftGR.MC.screen instanceof ReceivingLevelScreen) return true;
        else if (CraftGR.MC.screen instanceof ProgressScreen) return true;
        else if (CraftGR.MC.screen instanceof ConnectScreen) return true;
        else if (CraftGR.MC.screen instanceof GenericDirtMessageScreen) return true;

        if (CraftGR.PLATFORM.getPlatform() == Platform.PlatformType.FABRIC) {
            if (CraftGR.PLATFORM.isModLoaded("modmenu")) {
                if (CraftGR.MC.screen instanceof ModsScreen) return true;
            }
        }
        if (CraftGR.PLATFORM.isInForgeModMenu()) return true;

        if (CraftGR.PLATFORM.isModLoaded("cloth-config2") || CraftGR.PLATFORM.isModLoaded("cloth-config")) {
            if (CraftGR.MC.screen instanceof ConfigScreen) return true;
        }

        OverlayVisibility visibility = GRConfig.getConfig().overlayVisibility;

        if (visibility == OverlayVisibility.NONE) return true;
        if (visibility == OverlayVisibility.CHAT && !(CraftGR.MC.screen instanceof ChatScreen)) return true;

        Song currentSong = SongHandler.getInstance().song;

        if (currentSong != null && GRConfig.getConfig().openAlbum) {
            if (currentSong.intermission) return true;

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
        Song currentSong = SongHandler.getInstance().song;

        Font font = CraftGR.MC.font;

        int maxWidth = 0;
        if (currentSong.intermission) {
            maxWidth = font.width(new TranslatableComponent("text.craftgr.song.intermission"));
        } else {
            String[] strings = {currentSong.title, currentSong.artist, currentSong.album, currentSong.circle};
            for (String string : strings) {
                int width = font.width(string);
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
                ResourceLocation albumArt = null;
                try {
                    Request request = new Request.Builder().url(GRConfig.getConfig().url.albumArtURL + song.albumArt).build();

                    Response response = CraftGR.HTTP_CLIENT.newCall(request).execute();
                    InputStream stream = response.body().byteStream();

                    //Wait for texture manager to be initialized
                    while (CraftGR.MC.getTextureManager() == null) {
                        Thread.sleep(1);
                    }

                    albumArt = CraftGR.MC.getTextureManager().register("craftgr_album", new DynamicTexture(NativeImage.read(stream)));
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
        double mcScale = CraftGR.MC.getWindow().getGuiScale();

        return (float) ((((double) BASE_SCALE) * (((double) BASE_SCALE) / mcScale)) * uiScale);
    }

    public static SongInfoOverlay getInstance() {
        return INSTANCE;
    }
}
