package taz.development.discordhealtz.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import taz.development.discordhealtz.discord.commands.membros.SkillsDs;
import taz.development.discordhealtz.discord.commands.membros.VincularDs;
import taz.development.discordhealtz.discord.commands.staff.ConfigChannels;
import taz.development.discordhealtz.discord.commands.staff.TicketsOption;
import taz.development.discordhealtz.discord.utils.TicketGenerator;
import taz.development.discordhealtz.discord.utils.ticket.CloseTicket;
import taz.development.discordhealtz.utils.ConfigManager;

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
                TicketsOption tickets = new TicketsOption();
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

            TicketGenerator newTicket = new TicketGenerator();
            newTicket.generateTicket(event, configManager, categoryKey, channelNamePrefix);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        CloseTicket close = new CloseTicket(configManager);

        switch (event.getComponentId()) {
            case "close_ticket":
                close.Close(event);
                break;
            case "confirm_close_ticket":
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                close.ConfirmClose(event);
                break;
            case "cancel_close_ticket":
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                break;
            case "reopen_ticket":
                close.ReopenTicket(event);
                break;
            case "delete_ticket":
                close.DeleteTicket(event);
                break;
            default:
                event.reply("Ação inválida.").setEphemeral(true).queue();
                break;
        }
    }
}
