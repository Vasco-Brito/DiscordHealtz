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

    private final String[] optionList = new String[]{"obter", "ticketChannel", "VinculadoID"};

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

        // Adicionando uma imagem no topo do embed
        embed.setImage("https://example.com/your-image.png"); // Substitua pela URL da sua imagem

        // Adicionando o campo Role verificado
        embed.addField("Role verificado:", configManager.getRoleVerified(), false);
        embed.addField("Ticket Channel:", configManager.getTicketChannel(), false);
        embed.addField("Vinculado ID:", configManager.getVinculadoID(), false);

        // Espaço para seções adicionais
        embed.addBlankField(false);
        embed.addBlankField(false);
        embed.addBlankField(false);

        // Adicionando um rodapé
        embed.setFooter("Healtz Craft || C Todos os direitos reservados 2024");

        event.replyEmbeds(embed.build()).queue();
    }

    public void ticketChannelConfigChannel(SlashCommandInteractionEvent event, ConfigManager configManager) {
        // Lógica para ticketChannel
    }

    public void vinculadoIDConfigChannel(SlashCommandInteractionEvent event, ConfigManager configManager) {
        // Lógica para vinculadoID
    }
}
