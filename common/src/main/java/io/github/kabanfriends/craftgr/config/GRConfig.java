package io.github.kabanfriends.craftgr.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.Level;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class GRConfig {

    private static final Path CONFIG_DIR_PATH = Path.of("config");
    private static final Path CONFIG_FILE_PATH = Path.of("config", "craftgr.json");

    private JsonObject configJson;
    private Map<String, GRConfigOption> configMap = new HashMap<>();

    public abstract Screen getConfigScreen(Screen parent);

    public void init() {
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

        for (GRConfigCategory grc : GRConfigOptions.categories) {
            for (GRConfigOption value : grc.getOptions()) {
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
            CraftGR.log(Level.ERROR, "Failed to save mod config (craftgr.json)!");
            e.printStackTrace();
        }
    }

    public GRConfigOption getConfigEntry(String key) {
        return configMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) configMap.get(key).getValue();
    }

    public void setValue(String key, Object value) {
        setValue(CraftGR.getConfig().getConfigEntry(key), value);
    }

    public void setValue(GRConfigOption entry, Object value) {
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
