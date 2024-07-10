package taz.development.discordhealtz.discord.commands.membros;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import taz.development.discordhealtz.discord.startUp;
import taz.development.discordhealtz.utils.ConfigManager;

import static taz.development.discordhealtz.discord.utils.DiscordIds.VINCULADO_ROLE_ID;

public class VincularDs {

    public void vincularCmd(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String code = event.getOption("codigo").getAsString();
        String userId = event.getUser().getId();

        // Procura pelo código no arquivo YML
        String playerName = configManager.getPlayerByCode(code);
        if (playerName != null) {
            Role role = startUp.getGUILD().getRoleById(VINCULADO_ROLE_ID.getId());
            startUp.getGUILD().addRoleToMember(event.getUser(), role).queue(
                    success -> event.reply("Sua conta do Discord foi vinculada com sucesso ao jogador " + playerName).queue(),
                    failure -> {
                        System.err.println("Erro ao adicionar a role: " + failure.getMessage());
                        event.reply("Houve um erro ao tentar vincular sua conta do Discord. Crie um ticket para falar com a staff.").queue();
                    }
            );
            // Atualiza o DiscordUUID no arquivo YML
            configManager.updateDiscordUUID(playerName, userId);
        } else {
            event.reply("Código de vinculação inválido.").queue();
        }
    }

}
