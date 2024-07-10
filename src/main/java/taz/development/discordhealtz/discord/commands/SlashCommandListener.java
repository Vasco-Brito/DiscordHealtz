package taz.development.discordhealtz.discord.commands;

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
            case "config":
                ConfigChannels configChannels = new ConfigChannels();
                switch (event.getOption("option").getAsString()) {
                    case "obter":
                        configChannels.obterConfigChannel(event, configManager);
                        break;
                    case "ticketChannel":
                        break;
                    case "VinculadoID":
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
            String selectedOption = event.getValues().get(0); // Pega o primeiro valor selecionado
            System.out.println("Opção selecionada: " + selectedOption);
            // Adicione a lógica para lidar com cada opção selecionada
            switch (selectedOption) {
                case "ticket:compras_site":
                    // Lógica para "Compras no site"
                    break;
                case "ticket:reportar_bug":
                    // Lógica para "Reportar um bug"
                    break;
                case "ticket:denunciar_jogador":
                    // Lógica para "Denunciar um jogador"
                    break;
                case "ticket:outros_motivos":
                    // Lógica para "Outros motivos"
                    break;
                default:
                    break;
            }
            event.reply("Você selecionou: " + selectedOption).setEphemeral(true).queue(); // Responda à interação
        }
    }
}
