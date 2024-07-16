package taz.development.discordhealtz.discord.utils.ticket;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import taz.development.discordhealtz.utils.ConfigManager;

import java.awt.Color;
import java.util.EnumSet;

public class CloseTicket {

    private final ConfigManager configManager;

    public CloseTicket(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void Close(ButtonInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("Confirmar Fechamento de Ticket");
        embed.setDescription("Tem a certeza de que gostaria de encerrar seu atendimento?");

        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.success("confirm_close_ticket", "Confirmar")
                                .withEmoji(Emoji.fromUnicode("✅")),
                        Button.secondary("cancel_close_ticket", "Cancelar")
                                .withEmoji(Emoji.fromUnicode("❌"))
                ).setEphemeral(true).queue();
    }

    public void ConfirmClose(ButtonInteractionEvent event) {
        String userId = event.getUser().getId();

        event.getGuild().retrieveMemberById(userId).queue(member -> {
            if (member != null && event.getChannel() instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) event.getChannel();

                // Deny MESSAGE_SEND permission to everyone
                textChannel.upsertPermissionOverride(event.getGuild().getPublicRole())
                        .deny(Permission.MESSAGE_SEND).queue();
                for (Member m : textChannel.getMembers()) {
                    textChannel.upsertPermissionOverride(m)
                            .deny(Permission.MESSAGE_SEND).queue();
                }

                // Criar o embed de fechamento do ticket
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("[ / ] Ticket");
                embed.setDescription("Ticket fechado por " + event.getUser().getAsMention());
                embed.addField("Suporte dos tickets ou algo", "", false);

                // Enviar a mensagem com o embed e os botões de controle
                textChannel.sendMessageEmbeds(embed.build())
                        .addActionRow(
                                Button.primary("reopen_ticket", "Reabrir Ticket")
                                        .withEmoji(Emoji.fromUnicode("🔄")),
                                Button.danger("delete_ticket", "Deletar")
                                        .withEmoji(Emoji.fromUnicode("🗑️"))
                        ).queue();
            } else {
                event.getHook().sendMessage("Erro ao fechar o ticket.").queue();
            }
        });
    }

    public void ReopenTicket(ButtonInteractionEvent event) {
        if (isUserSupport(event.getMember())) {
            TextChannel textChannel = (TextChannel) event.getChannel();

            // Remove the MESSAGE_SEND deny permission from everyone
            textChannel.upsertPermissionOverride(event.getGuild().getPublicRole())
                    .clear(Permission.MESSAGE_SEND).queue();
            for (Member m : textChannel.getMembers()) {
                textChannel.upsertPermissionOverride(m)
                        .clear(Permission.MESSAGE_SEND).queue();
            }

            // Acknowledge the interaction and remove the buttons from the message
            event.getHook().editOriginalComponents().queue();
            event.reply("Ticket reaberto.").setEphemeral(true).queue();
        } else {
            event.reply("Você não tem permissão para reabrir este ticket.").setEphemeral(true).queue();
        }
    }

    public void DeleteTicket(ButtonInteractionEvent event) {
        if (isUserSupport(event.getMember())) {
            // Lógica para deletar o ticket
            event.getChannel().delete().queue();
        } else {
            event.reply("Você não tem permissão para deletar este ticket.").setEphemeral(true).queue();
        }
    }

    private boolean isUserSupport(Member member) {
        String supportRoleId = configManager.getSupportRole();
        return member.getRoles().stream().anyMatch(role -> role.getId().equals(supportRoleId));
    }
}
