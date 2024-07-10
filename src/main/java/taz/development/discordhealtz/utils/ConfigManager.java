package taz.development.discordhealtz.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final File file;
    private final FileConfiguration config;

    public ConfigManager(File dataFolder) {
        file = new File(dataFolder, "players.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void setPlayerData(String playerName, String playerUUID, String discordUUID, String code) {
        config.set(playerName + ".MUUID", playerUUID);
        config.set(playerName + ".DUUID", discordUUID);
        config.set(playerName + ".Codigo", code);
        saveConfig();
    }

    public String getPlayerCode(String playerName) {
        return config.getString(playerName + ".Codigo");
    }

    public String getPlayerByCode(String code) {
        for (String key : config.getKeys(false)) {
            if (code.equals(config.getString(key + ".Codigo"))) {
                return key;
            }
        }
        return null;
    }

    public void updateDiscordUUID(String playerName, String discordUUID) {
        config.set(playerName + ".DUUID", discordUUID);
        config.set("DiscordUUIDs." + discordUUID, playerName);
        saveConfig();
    }

    public boolean isDiscordLinked(String userId) {
        boolean linked = config.contains("DiscordUUIDs." + userId);
        System.out.println("Verifying Discord linked for user ID: " + userId + ", Result: " + linked);
        return linked;
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
