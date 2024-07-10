package taz.development.discordhealtz.discord.commands.membros;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.user.SkillsUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.entity.Player;
import taz.development.discordhealtz.DiscordHealtz;
import taz.development.discordhealtz.utils.ConfigManager;

import java.util.UUID;

public class SkillsDs {

    public void skills(SlashCommandInteractionEvent event, ConfigManager configManager) {
        String playerName = event.getOption("jogador").getAsString();
        Player player = DiscordHealtz.instance.getServer().getPlayer(playerName);
        UUID u = UUID.fromString("f36b4805-77b6-4a2c-b4c5-bb67308712b8");
        AuraSkillsApi auraSkills = AuraSkillsApi.get();
        SkillsUser userManager = auraSkills.getUserManager().getUser(u);

        for(Skill t : Skills.values()) {
            System.out.println(t.getDisplayName(userManager.getLocale()) + " " + userManager.getSkillLevel(t));
        }

        if (player != null) {
            //String subOption = event.getOption("skill").getAsString();

            event.reply("Teste de mensagem apenas").queue();

        } else {
            event.reply("O jogador não se encontra online").queue();
        }
    }

}
