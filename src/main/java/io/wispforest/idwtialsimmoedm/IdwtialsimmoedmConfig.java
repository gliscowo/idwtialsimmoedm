package io.wispforest.idwtialsimmoedm;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdwtialsimmoedmConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private static IdwtialsimmoedmConfig INSTANCE = new IdwtialsimmoedmConfig();

    public String descriptionPrefix = " ◇ ";
    public String descriptionIndent = "   ";

    public boolean displayOnlyWhenShiftIsHeld = false;
    public boolean displayOnBooksOnly = true;

    public static void load() {
        if (!Files.exists(configPath())) {
            save();
            return;
        }

        try (var input = Files.newInputStream(configPath())) {
            INSTANCE = GSON.fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), IdwtialsimmoedmConfig.class);
        } catch (IOException e) {
            IdwtialsimmoedmClient.LOGGER.warn("Could not load config", e);
        }
    }

    public static void save() {
        IdwtialsimmoedmClient.clearCache();

        try (var output = Files.newOutputStream(configPath()); var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            IdwtialsimmoedmClient.LOGGER.warn("Could not save config", e);
        }
    }

    public static IdwtialsimmoedmConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new IdwtialsimmoedmConfig();
        }

        return INSTANCE;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("idwtialsimmoedm.json");
    }
}
