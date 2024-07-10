package taz.development.discordhealtz.discord.commands.staff;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import taz.development.discordhealtz.utils.ConfigManager;

public class Tickets {

    public void tickets(SlashCommandInteractionEvent event, ConfigManager configManager) {
        event.reply("Teste de uma mensagem")
                .addActionRow(
                        StringSelectMenu.create("ticket:options")
                                .setPlaceholder("Selecione uma categoria")
                                .addOption("Compras no site", "ticket:compras_site", Emoji.fromUnicode("🛒"))
                                .addOption("Reportar um bug", "ticket:reportar_bug", Emoji.fromUnicode("🔧"))
                                .addOption("Denunciar um jogador", "ticket:denunciar_jogador", Emoji.fromUnicode("🕵️"))
                                .addOption("Outros motivos", "ticket:outros_motivos", Emoji.fromUnicode("🧰"))
                                .build()
                )
                .queue();
    }
}
