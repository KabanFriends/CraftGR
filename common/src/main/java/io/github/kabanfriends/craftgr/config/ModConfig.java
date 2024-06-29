package io.github.kabanfriends.craftgr.config;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.impl.*;
import io.github.kabanfriends.craftgr.config.entry.impl.EnumConfigField;
import io.github.kabanfriends.craftgr.overlay.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.SongProviderType;
import io.github.kabanfriends.craftgr.util.ExceptionUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class ModConfig {

    private static final Path CONFIG_DIR_PATH = Path.of("config");
    private static final Path CONFIG_FILE_PATH = Path.of("config", "craftgr.json");

    private static final Component CONFIG_TITLE = Component.translatable("text.craftgr.config.title");

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ConfigGroup[] CONFIG_GROUPS = {
            new ConfigGroup(Component.translatable("text.craftgr.config.category.playback"), true,
                    new RadioStateConfigField("playback"),
                    new IntegerConfigField("volume", 50)
                            .setFormatter((value) -> Component.literal(value + "%"))
                            .setRange(0, 100)
            ),
            new ConfigGroup(Component.translatable("text.craftgr.config.category.overlay"), true,
                    new EnumConfigField("overlayVisibility", SongInfoOverlay.OverlayVisibility.MENU),
                    new EnumConfigField("overlayPosition", SongInfoOverlay.OverlayPosition.TOP_RIGHT),
                    new BooleanConfigField("hideAlbumArt", false),
                    new BooleanConfigField("openAlbum", true),
                    new IntegerConfigField("overlayWidth", 115)
                            .setFormatter((value) -> Component.literal(value + "px"))
                            .setRange(35, 435)
                            .onApply((value) -> {
                                SongInfoOverlay overlay = CraftGR.getInstance().getSongInfoOverlay();
                                if (overlay != null) {
                                    overlay.updateScrollWidth();
                                }
                            }),
                    new FloatConfigField("overlayScale", 1.0f)
                            .setFormatter(value -> Component.literal(value + "×")),
                    new ColorConfigField("overlayBgColor", new Color(99, 34, 121)),
                    new ColorConfigField("overlayBarColor", new Color(160, 150, 174))
            ),
            new ConfigGroup(Component.translatable("text.craftgr.config.category.url"), true,
                    new EnumConfigField("songProvider", SongProviderType.JSON_API)
                            .onApply((value) -> {
                                CraftGR.getInstance().setSongProvider(((SongProviderType) value).createProvider());
                            }),
                    new StringConfigField("urlStream", "https://stream.gensokyoradio.net/1/"),
                    new StringConfigField("urlInfoJson", "https://gensokyoradio.net/api/station/playing/"),
                    new StringConfigField("urlAlbumArt", "https://gensokyoradio.net/images/albums/500/"),
                    new StringConfigField("urlWebSocket", "wss://gensokyoradio.net/wss"),
                    new IntegerConfigField("connectTimeout", 20_000)
                            .setFormatter((value) -> Component.literal(value + "ms")),
                    new IntegerConfigField("socketTimeout", 10_000)
                            .setFormatter((value) -> Component.literal(value + "ms"))
            )
    };

    private final CraftGR craftGR;
    private final Map<String, ConfigField<?>> configMap = Maps.newHashMap();

    private JsonObject configJson;

    public ModConfig(CraftGR craftGR) {
        this.craftGR = craftGR;
        load();
    }

    public void load() {
        if (Files.exists(CONFIG_FILE_PATH)) {
            try (Stream<String> stream = Files.lines(CONFIG_FILE_PATH)) {
                StringBuilder sb = new StringBuilder();
                stream.forEach(sb::append);
                String jstr = sb.toString();
                configJson = JsonParser.parseString(jstr).getAsJsonObject();
            } catch (Exception e) {
                configJson = new JsonObject();
                craftGR.log(Level.ERROR, "Failed to read mod config (craftgr.json): " + ExceptionUtil.getStackTrace(e));
            }
        } else {
            configJson = new JsonObject();
        }

        for (ConfigGroup grc : CONFIG_GROUPS) {
            for (ConfigField<?> value : grc.getFields()) {
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
                    craftGR.log(Level.ERROR, "Failed to read config value for " + value.getKey() + ": " + ExceptionUtil.getStackTrace(e));
                }
            }
        }
    }

    public void save() {
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
            craftGR.log(Level.ERROR, "Failed to save mod config (craftgr.json): " + ExceptionUtil.getStackTrace(e));
        }
    }

    @SuppressWarnings("unchecked")
    public Screen createScreen(Screen parent) {
        YetAnotherConfigLib.Builder config = YetAnotherConfigLib.createBuilder();
        config.title(CONFIG_TITLE);

        ConfigCategory.Builder category = ConfigCategory.createBuilder();
        category.name(CONFIG_TITLE);

        for (ConfigGroup configGroup : CONFIG_GROUPS) {
            OptionGroup.Builder group = OptionGroup.createBuilder();
            group.name(configGroup.getTitle());
            group.collapsed(!configGroup.getExpanded());

            for (ConfigField<?> entry : configGroup.getFields()) {
                group.option(entry.getOptionProvider().getOption(this));
            }

            category.group(group.build());
        }

        config.category(category.build());
        config.save(this::save);

        return config.build().generateScreen(parent);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) configMap.get(key).getValue();
    }

    public void setValue(String key, Object value) {
        setValue(configMap.get(key), value);
    }

    public void setValue(ConfigField<?> entry, Object value) {
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

    //<editor-fold desc="Utility">
    public static <T> T get(String key) {
        return CraftGR.getInstance().getConfig().getValue(key);
    }

    public static void set(String key, Object value) {
        CraftGR.getInstance().getConfig().setValue(key, value);
    }
    //</editor-fold>
}
