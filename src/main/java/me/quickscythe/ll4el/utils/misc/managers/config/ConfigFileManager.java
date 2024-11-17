package me.quickscythe.ll4el.utils.misc.managers.config;

import me.quickscythe.ll4el.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.json2.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileManager {

    private static final Map<String, ConfigFile> FILE_MAP = new HashMap<>();

    public static ConfigFile getFile(String filename) {
        return getFile(filename, new JSONObject());
    }

    public static ConfigFile getFile(String filename, JSONObject defaults) {
        if (!FILE_MAP.containsKey(filename)) {

            File file = new File(CoreUtils.getPlugin().getDataFolder() + "/" + filename + ".json");
            if (!file.exists()) {
                try {
                    if (!file.createNewFile())
                        throw new IOException("Couldn't create file (" + filename + ".json)");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ConfigFile config = new ConfigFile(file, defaults);
            FILE_MAP.put(filename, config);
        }
        return FILE_MAP.get(filename);
    }

    public static ConfigFile getFile(String filename, InputStream resource) {
        Bukkit.broadcastMessage("1");
        JSONObject defaults = new JSONObject();
        Bukkit.broadcastMessage("2");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            Bukkit.broadcastMessage("3");
            StringBuilder data = new StringBuilder();
            String line;
            Bukkit.broadcastMessage("4");
            while ((line = reader.readLine()) != null) {
                Bukkit.broadcastMessage("5");
                data.append(line);
            }
            Bukkit.broadcastMessage("6");
            defaults = data.toString().isEmpty() ? defaults : new JSONObject(data.toString());
            Bukkit.broadcastMessage(filename + ": " + defaults);
        } catch (IOException e) {
            Bukkit.broadcastMessage("7");
            throw new RuntimeException("There was an error generating the loot tables.");
        }
        Bukkit.broadcastMessage("8");
        return getFile(filename, defaults);
    }
}
