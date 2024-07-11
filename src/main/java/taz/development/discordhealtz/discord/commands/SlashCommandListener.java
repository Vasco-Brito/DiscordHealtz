package taz.development.discordhealtz.discord.commands;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import taz.development.discordhealtz.discord.commands.membros.SkillsDs;
import taz.development.discordhealtz.discord.commands.membros.VincularDs;
import taz.development.discordhealtz.discord.commands.staff.ConfigChannels;
import taz.development.discordhealtz.discord.commands.staff.Tickets;
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
                Tickets tickets = new Tickets();
                tickets.tickets(event, configManager);
                break;
            case "config":
                ConfigChannels configChannels = new ConfigChannels();
                switch (event.getOption("option").getAsString()) {
                    case "obter":
                        configChannels.obterConfigChannel(event, configManager);
                        break;
                    case "ticketChannel":
                        configChannels.ticketChannelConfigChannel(event, configManager);
                        break;
                    case "VinculadoID":
                        configChannels.vinculadoIDConfigChannel(event, configManager);
                        break;
                    case "CategoriaTicket":
                        configChannels.ticketCategotyConfigChannel(event, configManager);
                        break;
                    default:
                        break;
                }
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
            String selectedOption = event.getValues().get(0);
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
                event.reply("Canal de ticket criado: " + textChannel.getAsMention()).setEphemeral(true).queue();
                configManager.incrementCounter(categoryKey);
            });
        }
    }
}
