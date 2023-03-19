package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.config.builder.GRConfigBuilder;
import io.github.kabanfriends.craftgr.config.builder.GRConfigCategoryBuilder;
import io.github.kabanfriends.craftgr.config.value.impl.*;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class GRConfigOptions {

    public static GRConfigCategory[] categories;

    public static void init(GRConfigBuilder builder) {
        GRConfigCategoryBuilder playback = builder.getCategoryBuilder();
        playback.setTitle(Component.translatable("text.craftgr.config.category.playback"));
        playback.addRadioState("playback");
        playback.addPercentage("volume", 50);

        GRConfigCategoryBuilder overlay = builder.getCategoryBuilder();
        overlay.setTitle(Component.translatable("text.craftgr.config.category.overlay"));
        overlay.addEnum("overlayVisibility", SongInfoOverlay.OverlayVisibility.MENU);
        overlay.addEnum("overlayPosition", SongInfoOverlay.OverlayPosition.TOP_RIGHT);
        overlay.addBoolean("hideAlbumArt", false);
        overlay.addBoolean("openAlbum", true);
        overlay.addBoolean("hoverToExpand", true);
        overlay.addOverlayWidth("overlayWidth", 115);
        overlay.addFloat("overlayScale", 1.0f);
        overlay.addColor("overlayBgColor", new Color(99, 34, 121));
        overlay.addColor("overlayBarColor", new Color(160, 150, 174));

        GRConfigCategoryBuilder url = builder.getCategoryBuilder();
        url.setTitle(Component.translatable("text.craftgr.config.category.url"));
        url.addString("urlStream", "https://stream.gensokyoradio.net/1/");
        url.addString("urlInfoJson", "https://gensokyoradio.net/api/station/playing/");
        url.addString("urlAlbumArt", "https://gensokyoradio.net/images/albums/500/");

        builder.addCategory(playback.build());
        builder.addCategory(overlay.build());
        builder.addCategory(url.build());

        categories = builder.build();
    }
}
