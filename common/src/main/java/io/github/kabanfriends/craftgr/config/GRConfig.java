package io.github.kabanfriends.craftgr.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.entry.builder.RadioStateBuilder;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import io.github.kabanfriends.craftgr.config.value.impl.*;
import io.github.kabanfriends.craftgr.config.value.impl.EnumConfigValue;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
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
    private static Map<String, GRConfigValue> configMap = new HashMap<>();

    private static final GRConfigCategory[] categories = {
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.playback"), false,
                    new RadioStateConfigValue("playback"),
                    new PercentageConfigValue("volume", 50)
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.overlay"), false,
                    new EnumConfigValue("overlayVisibility", SongInfoOverlay.OverlayVisibility.MENU),
                    new EnumConfigValue("overlayPosition", SongInfoOverlay.OverlayPosition.TOP_RIGHT),
                    new BooleanConfigValue("hideAlbumArt", false),
                    new BooleanConfigValue("openAlbum", true),
                    new OverlayWidthConfigValue("overlayWidth", 115),
                    new FloatConfigValue("overlayScale", 1.0f),
                    new ColorConfigValue("overlayBgColor", 0x632279),
                    new ColorConfigValue("overlayBarColor", 0xA096AE)
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.url"), false,
                    new StringConfigValue("urlStream", "https://stream.gensokyoradio.net/1/"),
                    new StringConfigValue("urlInfoJson", "https://gensokyoradio.net/api/station/playing/"),
                    new StringConfigValue("urlAlbumArt", "https://gensokyoradio.net/images/albums/500/")
            )
    };

    @SuppressWarnings("unchecked")
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create();

        Component title = Component.translatable("text.craftgr.config.title");
        builder.setTitle(title);
        builder.setParentScreen(parent);
        ConfigCategory root = builder.getOrCreateCategory(title);

        for (GRConfigCategory grc : categories) {
            SubCategoryBuilder category = builder.entryBuilder().startSubCategory(grc.getTitle());
            category.setExpanded(grc.getExpanded());

            for (GRConfigValue entry : grc.getValues()) {
                AbstractFieldBuilder field = entry.getBuilder(builder.entryBuilder());
                field.setTooltip(Component.translatable("text.craftgr.config.option." + entry.getKey() + ".tooltip"));
                field.setSaveConsumer(value -> GRConfig.setValue(entry, value));
                category.add(field.build());
            }

            root.addEntry(category.build());
        }

        builder.setSavingRunnable(GRConfig::save);

        return builder.build();
    }

    public static void init() {
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
            for (GRConfigValue value : grc.getValues()) {
                configMap.put(value.getKey(), value);
                try {
                    if (configJson.has(value.getKey())) {
                        JsonPrimitive jsonValue = configJson.getAsJsonPrimitive(value.getKey());
                        Object realValue = value.deserialize(jsonValue);
                        if (realValue != null) {
                            value.setValue(realValue);
                        }
                    }
                } catch (Exception e) {
                    CraftGR.log(Level.ERROR, "Failed to read config value for " + value.getKey() + "!");
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

    public static GRConfigValue getConfigEntry(String key) {
        return configMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key) {
        return (T) configMap.get(key).getValue();
    }

    public static void setValue(String key, Object value) {
        setValue(GRConfig.getConfigEntry(key), value);
    }

    public static void setValue(GRConfigValue entry, Object value) {
        if (entry.getValue().equals(value)) {
            return;
        }

        entry.setValue(value);
        if (entry.getValue().equals(entry.getDefaultValue())) {
            configJson.remove(entry.getKey());
        } else {
            JsonPrimitive jsonValue = entry.serialize();
            if (jsonValue != null) {
                configJson.add(entry.getKey(), jsonValue);
            }
        }
    }
}
