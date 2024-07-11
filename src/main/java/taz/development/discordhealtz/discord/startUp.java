package taz.development.discordhealtz.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import taz.development.discordhealtz.DiscordHealtz;
import taz.development.discordhealtz.discord.commands.SlashCommandListener;
import taz.development.discordhealtz.discord.utils.DiscordIds;
import taz.development.discordhealtz.utils.ConfigManager;

public class startUp extends ListenerAdapter {

    static String BOT_TOKEN = "";
    static Guild GUILD;

    public static JDA discordInit(ConfigManager configManager, DiscordHealtz discordHealtz) throws Exception {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            throw new IllegalArgumentException("Bot token não foi fornecido.");
        }

        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new SlashCommandListener(configManager))
                .build();
        jda.awaitReady();

        GUILD = jda.getGuildById(DiscordIds.GUILD_ID.getId());

        configManager.loadIds();

        registerCommands(jda);

        return jda;
    }

    private static void clearCommands(JDA jda) {
        jda.updateCommands().queue(commands -> {
            commands.forEach(command -> jda.deleteCommandById(command.getId()).queue());
            System.out.println("Todos os comandos anteriores foram deletados.");
        });
    }

    public static void registerCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("vincular", "Vincula com sua conta do Minecraft.")
                        .addOption(OptionType.STRING, "codigo", "Código de vínculo (fornecido no Minecraft)", true),
                Commands.slash("skills", "Mostra as skills de um jogador")
                        .addOption(OptionType.STRING, "jogador", "Nome do jogador no Minecraft", true)
                        .addOption(OptionType.STRING, "skill", "Especifica o tipo da skill", false),

                Commands.slash("ticket", "Teste para os tickets")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS)),
                Commands.slash("config", "Altera algumas configurações")
                        .addOption(OptionType.STRING, "option", "opção", true, true)
                        .addOption(OptionType.STRING, "discordid", "ID pretendido", false, false) // Mudança aqui: "discordid" em minúsculas
        ).queue(
                success -> System.out.println("Novos comandos registados com sucesso!"),
                failure -> System.err.println("Falha ao registar novos comandos: " + failure.getMessage())
        );
        System.out.println(jda.retrieveCommands());
    }

    public static void updatePresence(JDA jda, DiscordHealtz discordHealtz) {
        Guild guild = jda.getGuildById(DiscordIds.GUILD_ID.getId()); // Obtém o Guild pelo ID correto

        if (guild == null) {
            System.out.println("Guild não encontrada.");
            return;
        }

        for (Member member : guild.getMembers()) {
            if (discordHealtz != null && discordHealtz.isPlayerOnline(member.getEffectiveName())) {
                assignOnlineRole(guild, member);
            } else {
                assignOfflineRole(guild, member);
            }
        }
    }

    public static Guild getGUILD() {
        return GUILD;
    }

    private static void assignOnlineRole(Guild guild, Member member) {
        if (guild == null || member == null) return;

        guild.removeRoleFromMember(member, guild.getRoleById(DiscordIds.OFFLINE_ROLE_ID.getId())).queue();
        guild.addRoleToMember(member, guild.getRoleById(DiscordIds.ONLINE_ROLE_ID.getId())).queue();
    }

    private static void assignOfflineRole(Guild guild, Member member) {
        if (guild == null || member == null) return;

        guild.removeRoleFromMember(member, guild.getRoleById(DiscordIds.ONLINE_ROLE_ID.getId())).queue();
        guild.addRoleToMember(member, guild.getRoleById(DiscordIds.OFFLINE_ROLE_ID.getId())).queue();
    }
}
