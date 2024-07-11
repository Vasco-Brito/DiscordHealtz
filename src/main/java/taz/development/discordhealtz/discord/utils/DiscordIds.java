package taz.development.discordhealtz.discord.utils;

public enum DiscordIds {
    GUILD_ID("1124606975288823839"),
    ROLE_VERIFIED("1259902609914069125"),
    TICKET_CHANNEL("9876543210"),
    TICKET_CATEGORY("13212132"),
    SUPPORT_ROLE("123456789"),

    ONLINE_ROLE_ID("1259617906287706203"),
    OFFLINE_ROLE_ID("1259617934557581443");


    private String id;

    DiscordIds(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
