package io.github.kabanfriends.craftgr.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.compat.ClothCompat;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.impl.*;
import io.github.kabanfriends.craftgr.config.entry.impl.EnumConfigEntry;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Level;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class GRConfig {

    private static final Path CONFIG_DIR_PATH = Path.of("config");
    private static final Path CONFIG_FILE_PATH = Path.of("config", "craftgr.json");

    private static JsonObject configJson;
    private static Map<String, GRConfigEntry> configMap = new HashMap<>();

    private static final GRConfigCategory[] categories = {
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.playback"), false,
                    new PercentageConfigEntry("volume", 50)
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.overlay"), false,
                    new EnumConfigEntry("overlayVisibility", SongInfoOverlay.OverlayVisibility.MENU),
                    new EnumConfigEntry("overlayPosition", SongInfoOverlay.OverlayPosition.TOP_RIGHT),
                    new BooleanConfigEntry("hideAlbumArt", false),
                    new BooleanConfigEntry("openAlbum", true),
                    new OverlayWidthConfigEntry("overlayWidth", 115),
                    new FloatConfigEntry("overlayScale", 1.0f),
                    new ColorConfigEntry("overlayBgColor", 0x632279),
                    new ColorConfigEntry("overlayBarColor", 0xA096AE)
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.url"), false,
                    new StringConfigEntry("urlStream", "https://stream.gensokyoradio.net/1/"),
                    new StringConfigEntry("urlInfoJson", "https://gensokyoradio.net/api/station/playing/"),
                    new StringConfigEntry("urlAlbumArt", "https://gensokyoradio.net/images/albums/500/")
            )
    };

    @SuppressWarnings("unchecked")
    public static Screen getConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create();

        Component title = Component.translatable("text.craftgr.config.title");
        builder.setTitle(title);
        ConfigCategory root = builder.getOrCreateCategory(title);

        for (GRConfigCategory grc : categories) {
            SubCategoryBuilder category = builder.entryBuilder().startSubCategory(grc.getTitle());
            category.setExpanded(grc.getExpanded());

            for (GRConfigEntry entry : grc.getEntries()) {

                FieldBuilder field = entry.getBuilder(builder.entryBuilder());
                ClothCompat.getCompat().setTooltip(field, Component.translatable("text.craftgr.config.option." + entry.getKey() + ".tooltip"));
                ClothCompat.getCompat().setSaveConsumer(field, value -> GRConfig.setValue(entry, value));
                category.add(field.build());
            }

            root.addEntry(category.build());
        }

        builder.setSavingRunnable(GRConfig::save);

        return builder.build();
    }

    public static void init() {
        ClothCompat.init();

        if (Files.exists(CONFIG_FILE_PATH)) {
            try {
                Stream<String> stream = Files.lines(CONFIG_FILE_PATH);
                StringBuilder sb = new StringBuilder();
                stream.forEach(sb::append);
                String jstr = sb.toString();
                configJson = JsonParser.parseString(jstr).getAsJsonObject();
            } catch (Exception e) {
                configJson = new JsonObject();
                CraftGR.log(Level.ERROR, "Failed to read mod config (craftgr.json)!");
                e.printStackTrace();
            }
        } else {
            configJson = new JsonObject();
        }

        for (GRConfigCategory grc : categories) {
            for (GRConfigEntry entry : grc.getEntries()) {
                configMap.put(entry.getKey(), entry);
                try {
                    if (configJson.has(entry.getKey())) {
                        JsonPrimitive value = configJson.getAsJsonPrimitive(entry.getKey());
                        entry.setValue(entry.deserialize(value));
                    }
                } catch (Exception e) {
                    CraftGR.log(Level.ERROR, "Failed to read config value for " + entry.getKey() + "!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR_PATH);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            JsonWriter jWriter = gson.newJsonWriter(new OutputStreamWriter(Files.newOutputStream(CONFIG_FILE_PATH), StandardCharsets.UTF_8));
            jWriter.setIndent("\t");
            gson.toJson(configJson, jWriter);
            jWriter.flush();
            jWriter.close();
        } catch (Exception e) {
            CraftGR.log(Level.ERROR, "Failed to save mod config (craftgr.json)!");
            e.printStackTrace();
        }
    }

    public static GRConfigEntry getConfigEntry(String key) {
        return configMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key) {
        return (T) configMap.get(key).getValue();
    }

    public static void setValue(String key, Object value) {
        setValue(GRConfig.getConfigEntry(key), value);
    }

    public static void setValue(GRConfigEntry entry, Object value) {
        if (entry.getValue().equals(value)) {
            return;
        }

        entry.setValue(value);
        if (entry.getValue().equals(entry.getDefaultValue())) {
            configJson.remove(entry.getKey());
        } else {
            configJson.add(entry.getKey(), entry.serialize());
        }
    }
}
