package taz.development.discordhealtz.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import taz.development.discordhealtz.discord.utils.DiscordIds;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {

    private final File playersFile;
    private final FileConfiguration playersConfig;

    private final File discordFile;
    private final FileConfiguration discordConfig;

    public ConfigManager(File dataFolder) {
        playersFile = new File(dataFolder, "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        discordFile = new File(dataFolder, "discord.yml");
        if (!discordFile.exists()) {
            try {
                copyResourceToFile("discord.yml", discordFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        discordConfig = YamlConfiguration.loadConfiguration(discordFile);
        loadIds();
    }

    private void copyResourceToFile(String resourcePath, File destination) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            Files.copy(in, destination.toPath());
        }
    }

    public void loadIds() {
        DiscordIds.ROLE_VERIFIED.setId(discordConfig.getString("role_verified"));
        DiscordIds.TICKET_CHANNEL.setId(discordConfig.getString("ticket_channel"));
        DiscordIds.TICKET_CATEGORY.setId(discordConfig.getString("ticket_category"));
    }

    public void setPlayerData(String playerName, String playerUUID, String discordUUID, String code) {
        playersConfig.set(playerName + ".MUUID", playerUUID);
        playersConfig.set(playerName + ".DUUID", discordUUID);
        playersConfig.set(playerName + ".Codigo", code);
        savePlayersConfig();
    }

    public String getPlayerCode(String playerName) {
        return playersConfig.getString(playerName + ".Codigo");
    }

    public String getPlayerByCode(String code) {
        for (String key : playersConfig.getKeys(false)) {
            if (code.equals(playersConfig.getString(key + ".Codigo"))) {
                return key;
            }
        }
        return null;
    }

    public void updateDiscordUUID(String playerName, String discordUUID) {
        playersConfig.set(playerName + ".DUUID", discordUUID);
        playersConfig.set("DiscordUUIDs." + discordUUID, playerName);
        savePlayersConfig();
    }

    public boolean isDiscordLinked(String userId) {
        boolean linked = playersConfig.contains("DiscordUUIDs." + userId);
        System.out.println("Verifying Discord linked for user ID: " + userId + ", Result: " + linked);
        return linked;
    }

    private void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDiscordConfig() {
        try {
            discordConfig.save(discordFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoleVerified() {
        return discordConfig.getString("role_verified");
    }

    public String getTicketChannel() {
        return discordConfig.getString("ticket_channel");
    }

    public String getTicketCategory() {
        return discordConfig.getString("ticket_category");
    }

    public void setRoleVerified(String roleId) {
        discordConfig.set("role_verified", roleId);
        saveDiscordConfig();
        DiscordIds.ROLE_VERIFIED.setId(roleId);
    }

    public void setTicketChannel(String channelId) {
        discordConfig.set("ticket_channel", channelId);
        saveDiscordConfig();
        DiscordIds.TICKET_CHANNEL.setId(channelId);
    }

    public void setTicketCategory(String categoryId) {
        discordConfig.set("ticket_category", categoryId);
        saveDiscordConfig();
        DiscordIds.TICKET_CATEGORY.setId(categoryId);
    }

    public int getCounter(String category) {
        return discordConfig.getInt("COUNTER_" + category.toUpperCase(), 0);
    }

    public void incrementCounter(String category) {
        String key = "COUNTER_" + category.toUpperCase();
        int current = discordConfig.getInt(key, 0);
        discordConfig.set(key, current + 1);
        saveDiscordConfig();
    }
}
