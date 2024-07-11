package taz.development.discordhealtz;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import taz.development.discordhealtz.discord.startUp;
import taz.development.discordhealtz.minecraft.commands.Vincular;
import taz.development.discordhealtz.utils.ConfigManager;

import java.io.File;

public final class DiscordHealtz extends JavaPlugin {

    private ConfigManager configManager;
    private JDA jda;
    @Getter
    public static DiscordHealtz instance;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(getDataFolder());
        try {
            this.jda = startUp.discordInit(configManager, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.getCommand("vincular").setExecutor(new Vincular(this, configManager));

        getServer().getScheduler().runTaskTimer(this, this::updateBotActivity, 0L, 20*60L);
    }

    @Override
    public void onDisable() {
    }

    public void updateBotActivity() {
        int onlinePlayers = getServer().getOnlinePlayers().size();
        int maxPlayers = getServer().getMaxPlayers();

        if (jda != null) {
            jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.customStatus(
                    "Jogadores online: " + onlinePlayers + "/" + maxPlayers
            ));
        }
    }

    public boolean isPlayerOnline(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        return player != null && player.isOnline();
    }
}
