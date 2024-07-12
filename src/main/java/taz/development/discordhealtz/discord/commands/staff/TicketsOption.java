package taz.development.discordhealtz.discord.commands.staff;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
import taz.development.discordhealtz.utils.ConfigManager;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TicketsOption {

    //Mensagem no sv do servidor
    //                    "<:mundo:1260970661321506966> [SITE](https://loja.healtzcraft.com) - <:MineCoins:1260970768733569075> [WIKI](https://healtz-craft-servidor-de-minecra.gitbook.io/healtz-craft) - <:Livro:1260970396115533897> [DISCORD](https://discord.gg/cCVHB2Uwg8)");

    public void tickets(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String ticketChannelId = configManager.getTicketChannel();
        event.reply("Mensagem de tickets criada com sucesso").setEphemeral(true).queue();

        String logoPath = "/img/logo.png";
        InputStream logoStream = getClass().getResourceAsStream(logoPath);

        if (logoStream == null) {
            event.getChannel().sendMessage("Erro: Arquivo de logo não encontrado.").queue();
            return;
        }

        try {
            File tempLogoFile = File.createTempFile("logo", ".png");
            Files.copy(logoStream, tempLogoFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String bannerPath = "/img/banner.png";
            InputStream bannerStream = getClass().getResourceAsStream(bannerPath);

            if (bannerStream == null) {
                event.getChannel().sendMessage("Erro: Arquivo de banner não encontrado.").queue();
                return;
            }

            File tempBannerFile = File.createTempFile("banner", ".png");
            Files.copy(bannerStream, tempBannerFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.CYAN);
            embed.setTitle("📢 Central de Atendimento");
            embed.setDescription("Olá, seja bem-vindo à Central de Atendimento da Healtz Craft.\n\n" +
                    "> Selecione uma categoria na lista abaixo para iniciar seu atendimento. Vale lembrar que o seu atendimento será fornecido através de um canal privado.\n\n" +
                    "\uD83C\uDF0E Links: [SITE](https://loja.healtzcraft.com) - \uD83D\uDCB8 [WIKI](https://healtz-craft-servidor-de-minecra.gitbook.io/healtz-craft) - \uD83D\uDCDA [DISCORD](https://discord.gg/cCVHB2Uwg8) \uD83C\uDFAE\n");
            embed.setFooter("Healtz Craft | Minecraft Brasil © Todos os direitos reservados 2024.", "attachment://logo.png");
            embed.setImage("attachment://banner.png");

            event.getJDA().getTextChannelById(ticketChannelId)
                    .sendMessageEmbeds(embed.build())
                    .addActionRow(
                            StringSelectMenu.create("ticket:options")
                                    .setPlaceholder("Selecione uma categoria")
                                    .addOption("Compras no site", "ticket:compras_site", Emoji.fromUnicode("🛒"))
                                    .addOption("Reportar um bug", "ticket:reportar_bug", Emoji.fromUnicode("🔧"))
                                    .addOption("Denunciar um jogador", "ticket:denunciar_jogador", Emoji.fromUnicode("🕵️"))
                                    .addOption("Outros motivos", "ticket:outros_motivos", Emoji.fromUnicode("🧰"))
                                    .build()
                    )
                    .addFiles(
                            FileUpload.fromData(tempLogoFile, "logo.png"),
                            FileUpload.fromData(tempBannerFile, "banner.png")
                    )
                    .queue();

            tempLogoFile.deleteOnExit();
            tempBannerFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
