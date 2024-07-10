package taz.development.discordhealtz.minecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import taz.development.discordhealtz.DiscordHealtz;
import taz.development.discordhealtz.utils.ConfigManager;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class Vincular implements CommandExecutor {

    private final ConfigManager configManager;
    private final Set<String> usedCodes = new HashSet<>();

    public Vincular(DiscordHealtz discordHealtz, ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
            return false;
        }

        Player player = (Player) sender;
        String playerName = player.getName();
        String playerUUID = player.getUniqueId().toString();

        String existingCode = configManager.getPlayerCode(playerName);
        if (existingCode != null) {
            player.sendMessage("Sua conta já está vinculada.");
            return true;
        }

        String codigo = generateUniqueCode();

        configManager.setPlayerData(playerName, playerUUID, "DiscordUUIDPlaceholder", codigo);

        sendCode(player, codigo);

        return true;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode(8);
        } while (usedCodes.contains(code));
        usedCodes.add(code);
        return code;
    }

    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    private void sendCode(Player player, String code) {
        player.sendMessage("Seu código de vinculação é: " + code + "\nCopie o código e use-o conforme necessário.");
    }
}
