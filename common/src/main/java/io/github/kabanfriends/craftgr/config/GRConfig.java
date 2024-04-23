package io.github.kabanfriends.craftgr.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.impl.*;
import io.github.kabanfriends.craftgr.config.entry.impl.EnumConfigEntry;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Level;

import java.awt.*;
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

    public static final String TITLE_KEY = "text.craftgr.config.title";

    private static JsonObject configJson;
    private static Map<String, GRConfigEntry> configMap = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final GRConfigCategory[] categories = {
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.playback"), true,
                    new RadioStateConfigEntry("playback"),
                    new PercentageConfigEntry("volume", 50)
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.overlay"), true,
                    new EnumConfigEntry("overlayVisibility", SongInfoOverlay.OverlayVisibility.MENU),
                    new EnumConfigEntry("overlayPosition", SongInfoOverlay.OverlayPosition.TOP_RIGHT),
                    new BooleanConfigEntry("hideAlbumArt", false),
                    new BooleanConfigEntry("openAlbum", true),
                    new OverlayWidthConfigEntry("overlayWidth", 115),
                    new FloatConfigEntry("overlayScale", 1.0f),
                    new ColorConfigEntry("overlayBgColor", new Color(99, 34, 121)),
                    new ColorConfigEntry("overlayBarColor", new Color(160, 150, 174))
            ),
            new GRConfigCategory(Component.translatable("text.craftgr.config.category.url"), true,
                    new StringConfigEntry("urlStream", "https://stream.gensokyoradio.net/1/"),
                    new StringConfigEntry("urlInfoJson", "https://gensokyoradio.net/api/station/playing/"),
                    new StringConfigEntry("urlAlbumArt", "https://gensokyoradio.net/images/albums/500/")
            )
    };

    @SuppressWarnings("unchecked")
    public static Screen getConfigScreen(Screen parent) {
        Component title = Component.translatable(TITLE_KEY);

        YetAnotherConfigLib.Builder config = YetAnotherConfigLib.createBuilder();
        config.title(title);

        ConfigCategory.Builder category = ConfigCategory.createBuilder();
        category.name(title);

        for (GRConfigCategory grc : categories) {
            OptionGroup.Builder group = OptionGroup.createBuilder();
            group.name(grc.getTitle());
            group.collapsed(!grc.getExpanded());

            for (GRConfigEntry<?> entry : grc.getEntries()) {
                group.option(entry.getOptionProvider().getOption());
            }

            category.group(group.build());
        }

        config.category(category.build());
        config.save(GRConfig::save);

        return config.build().generateScreen(parent);
    }

    public static void init() {
        if (Files.exists(CONFIG_FILE_PATH)) {
            try (Stream<String> stream = Files.lines(CONFIG_FILE_PATH)) {
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
            for (GRConfigEntry<?> value : grc.getEntries()) {
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

    public static GRConfigEntry<?> getConfigEntry(String key) {
        return configMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key) {
        return (T) configMap.get(key).getValue();
    }

    public static void setValue(String key, Object value) {
        setValue(GRConfig.getConfigEntry(key), value);
    }

    public static void setValue(GRConfigEntry<?> entry, Object value) {
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
