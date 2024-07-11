package taz.development.discordhealtz.discord.commands.staff;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import taz.development.discordhealtz.utils.ConfigManager;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigChannels {

    private final String[] optionList = new String[]{"obter", "ticketChannel", "VinculadoID", "CategoriaTicket"};

    public void configChannels(CommandAutoCompleteInteractionEvent event, ConfigManager configManager) {
        List<Command.Choice> options = Stream.of(optionList)
                .filter(option -> option.startsWith(event.getFocusedOption().getValue()))
                .map(option -> new Command.Choice(option, option))
                .collect(Collectors.toList());
        event.replyChoices(options).queue();
    }

    public void obterConfigChannel(@NotNull SlashCommandInteractionEvent event, ConfigManager configManager) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Configurações");
        embed.setColor(Color.BLACK);

        embed.addField("Role verificado:", configManager.getRoleVerified(), false);
        embed.addField("Ticket Channel:", configManager.getTicketChannel(), false);
        embed.addField("Categoria do Ticket:", configManager.getTicketCategory(), false);

        embed.setFooter("Healtz Craft | Minecraft Brasil © Todos os direitos reservados 2024.");

        event.replyEmbeds(embed.build()).queue();
    }

    public void ticketChannelConfigChannel(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String channelId = event.getOption("discordid").getAsString();
        configManager.setTicketChannel(channelId);
        event.reply("Canal de tickets configurado com o ID: " + channelId).queue();
    }

    public void vinculadoIDConfigChannel(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String roleId = event.getOption("discordid").getAsString();
        configManager.setRoleVerified(roleId);
        event.reply("Cargo para vinculação configurado com o ID: " + roleId).queue();
    }

    public void ticketCategotyConfigChannel(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String categoryId = event.getOption("discordid").getAsString();
        configManager.setTicketCategory(categoryId);
        event.reply("A categoria onde os tickets serão abertos foi vinculada com o ID: " + categoryId).queue();
    }
}
