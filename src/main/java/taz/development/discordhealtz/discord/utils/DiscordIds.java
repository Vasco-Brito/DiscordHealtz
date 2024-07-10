package taz.development.discordhealtz.discord.utils;

public enum DiscordIds {
    GUILD_ID("1124606975288823839"),
    VINCULADO_ROLE_ID("1259902609914069125"),
    ONLINE_ROLE_ID("1259617906287706203"),
    OFFLINE_ROLE_ID("1259617934557581443");

    private final String id;

    DiscordIds(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
