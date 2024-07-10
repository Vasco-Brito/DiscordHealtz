package taz.development.discordhealtz.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
                discordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        discordConfig = YamlConfiguration.loadConfiguration(discordFile);
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

    public String getVinculadoID() {
        return discordConfig.getString("vinculado_id");
    }

    public void setRoleVerified(String roleId) {
        discordConfig.set("role_verified", roleId);
        saveDiscordConfig();
    }

    public void setTicketChannel(String channelId) {
        discordConfig.set("ticket_channel", channelId);
        saveDiscordConfig();
    }

    public void setVinculadoID(String vinculadoId) {
        discordConfig.set("vinculado_id", vinculadoId);
        saveDiscordConfig();
    }
}
