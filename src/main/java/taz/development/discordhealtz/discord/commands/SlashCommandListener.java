package taz.development.discordhealtz.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import taz.development.discordhealtz.discord.commands.membros.SkillsDs;
import taz.development.discordhealtz.discord.commands.membros.VincularDs;
import taz.development.discordhealtz.discord.commands.staff.ConfigChannels;
import taz.development.discordhealtz.discord.commands.staff.Tickets;
import taz.development.discordhealtz.utils.ConfigManager;

import java.awt.Color;

public class SlashCommandListener extends ListenerAdapter {

    private final ConfigManager configManager;

    public SlashCommandListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName().toLowerCase()) {
            case "vincular":
                VincularDs vincular = new VincularDs();
                vincular.vincularCmd(event, configManager);
                break;
            case "skills":
                SkillsDs skills = new SkillsDs();
                skills.skills(event, configManager);
                break;
            case "ticket":
                Tickets tickets = new Tickets();
                tickets.tickets(event, configManager);
                break;
            case "config":
                ConfigChannels configChannels = new ConfigChannels();
                switch (event.getOption("option").getAsString().toLowerCase()) {
                    case "obter":
                        configChannels.obterConfigChannel(event, configManager);
                        break;
                    case "ticketchannel":
                        configChannels.ticketChannelConfigChannel(event, configManager);
                        break;
                    case "vinculadoid":
                        configChannels.vinculadoIDConfigChannel(event, configManager);
                        break;
                    case "categoriaticket":
                        configChannels.ticketCategoryConfigChannel(event, configManager);
                        break;
                    case "supportrole":
                        configChannels.supportRoleConfigChannel(event, configManager);
                        break;
                    default:
                        event.reply("Opção inválida.").setEphemeral(true).queue();
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().toLowerCase().equalsIgnoreCase("config") && event.getFocusedOption().getName().equals("option")) {
            ConfigChannels configChannels = new ConfigChannels();
            configChannels.configChannels(event, configManager);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticket:options")) {
            String selectedOption = event.getValues().get(0); // Pega o primeiro valor selecionado
            System.out.println("Opção selecionada: " + selectedOption);

            String categoryKey;
            String channelNamePrefix;
            switch (selectedOption) {
                case "ticket:compras_site":
                    categoryKey = "compras";
                    channelNamePrefix = "Compras";
                    break;
                case "ticket:reportar_bug":
                    categoryKey = "bugs";
                    channelNamePrefix = "Bugs";
                    break;
                case "ticket:denunciar_jogador":
                    categoryKey = "report";
                    channelNamePrefix = "Report";
                    break;
                case "ticket:outros_motivos":
                    categoryKey = "diversos";
                    channelNamePrefix = "Diversos";
                    break;
                default:
                    event.reply("Opção inválida.").setEphemeral(true).queue();
                    return;
            }

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

                var supportRole = event.getGuild().getRoleById(supportRoleId);
                var member = event.getGuild().getMemberById(userId);

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

                // Criar o embed para a mensagem de ticket
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.GREEN);
                embed.setTitle("Atendimento solicitado");
                embed.setDescription("Aguarde. Em breve, o suporte irá respondê-lo(a).\n\n" +
                        "• O atendimento é realizado de forma privada, somente integrantes do suporte vão ter acesso ao seu atendimento.");
                embed.setFooter("TicketToolXyz – Ticketing without clutter", null);

                // Enviar a mensagem com o embed e o botão para fechar o ticket
                textChannel.sendMessageEmbeds(embed.build())
                        .addActionRow(
                                net.dv8tion.jda.api.interactions.components.buttons.Button.danger("close_ticket", "Fechar Ticket")
                                        .withEmoji(Emoji.fromUnicode("🔒"))
                        ).queue(success -> {
                            System.out.println("Mensagem inicial enviada com sucesso.");
                            event.reply("Canal de ticket criado: " + textChannel.getAsMention()).setEphemeral(true).queue();
                            configManager.incrementCounter(categoryKey);
                        }, error -> {
                            System.err.println("Erro ao enviar mensagem inicial: " + error.getMessage());
                        });
            });
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("close_ticket")) {
            // Criar mensagem de confirmação com botões
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
        } else if (event.getComponentId().equals("confirm_close_ticket")) {
            event.deferEdit().queue();
            event.getChannel().delete().queue();
        } else if (event.getComponentId().equals("cancel_close_ticket")) {
            event.deferEdit().queue(); // Acknowledge the interaction first
            event.getHook().deleteOriginal().queue(); // Delete the confirmation message
        }
    }
}
