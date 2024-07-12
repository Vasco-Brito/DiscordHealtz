package taz.development.discordhealtz.discord.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import taz.development.discordhealtz.utils.ConfigManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TicketGenerator {

    public void generateTicket(StringSelectInteractionEvent event, ConfigManager configManager, String categoryKey, String channelNamePrefix) {
        Category category = event.getJDA().getCategoryById(configManager.getTicketCategory());
        if (category == null) {
            event.reply("Categoria de tickets não encontrada.").setEphemeral(true).queue();
            return;
        }

        int counter = configManager.getCounter(categoryKey);
        String channelName = String.format("%s-%03d", channelNamePrefix, counter + 1);

        category.createTextChannel(channelName).queue(textChannel -> {
            // Definir permissões para o canal de ticket
            String supportRoleId = configManager.getSupportRole();
            if (supportRoleId == null) {
                event.reply("Cargo de suporte não encontrado.").setEphemeral(true).queue();
                return;
            }
            String userId = event.getUser().getId();

            Role supportRole = event.getGuild().getRoleById(supportRoleId);

            event.getGuild().retrieveMemberById(userId).queue(member -> {
                if (supportRole == null || member == null) {
                    event.reply("Erro ao definir permissões para o ticket.").setEphemeral(true).queue();
                    return;
                }

                textChannel.upsertPermissionOverride(event.getGuild().getPublicRole())
                        .deny(Permission.VIEW_CHANNEL).queue();
                textChannel.upsertPermissionOverride(supportRole)
                        .grant(Permission.VIEW_CHANNEL).queue();
                textChannel.upsertPermissionOverride(member)
                        .grant(Permission.VIEW_CHANNEL).queue();

                String logoPath = "/img/logo.png";
                InputStream logoStream = getClass().getResourceAsStream(logoPath);

                event.getChannel().sendMessage("Teste 1");
                if (logoStream == null) {
                    event.getChannel().sendMessage("Erro: Arquivo de logo não encontrado.").queue();
                    return;
                }

                try {
                    File tempLogoFile = File.createTempFile("logo", ".png");
                    Files.copy(logoStream, tempLogoFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // Criar o embed para a mensagem de ticket
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.GREEN);
                    embed.setTitle("Atendimento solicitado");
                    embed.setDescription("Aguarde. Em breve, o suporte irá respondê-lo(a).\n\n" +
                            "• O atendimento é realizado de forma privada, somente integrantes do suporte vão ter acesso ao seu atendimento.");
                    embed.setFooter("Healtz Craft | Minecraft Brasil © Todos os direitos reservados 2024.", "attachment://logo.png");

                    // Enviar a mensagem com o embed e o botão para fechar o ticket
                    textChannel.sendMessageEmbeds(embed.build())
                            .addActionRow(
                                    net.dv8tion.jda.api.interactions.components.buttons.Button.danger("close_ticket", "Fechar Ticket")
                                            .withEmoji(Emoji.fromUnicode("🔒"))
                            ).addFiles(
                                    FileUpload.fromData(tempLogoFile, "logo.png")
                            )
                            .queue(success -> {
                                System.out.println("Mensagem inicial enviada com sucesso.");
                                event.reply("Canal de ticket criado: " + textChannel.getAsMention()).setEphemeral(true).queue();
                                configManager.incrementCounter(categoryKey);
                            }, error -> {
                                System.err.println("Erro ao enviar mensagem inicial: " + error.getMessage());
                            });
                } catch (IOException e) {
                    event.getChannel().sendMessage("Erro: " + e.getMessage()).queue();
                    e.printStackTrace();
                }
            });
        });
    }

}
