package taz.development.discordhealtz.discord.utils.ticket;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.awt.*;

public class CloseTicket {

    public void Close(ButtonInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("Confirmar Fechamento de Ticket");
        embed.setDescription("Tem a certeza de que gostaria de encerrar seu atendimento?");

        event.replyEmbeds(embed.build())
                .addActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("confirm_close_ticket", "Confirmar")
                                .withEmoji(Emoji.fromUnicode("✅")),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("cancel_close_ticket", "Cancelar")
                                .withEmoji(Emoji.fromUnicode("❌"))
                ).setEphemeral(true).queue();
    }

}
