package Shared;

import java.io.Serializable;

public class GameServer implements Serializable {
    String name;
    String description;
    String discordURL;

    public GameServer(String name, String description, String discordURL) {
        this.name = name;
        this.description = description;
        this.discordURL = discordURL;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GameServer)){return false;}
        final GameServer other = (GameServer) obj;
        return other.getDescription().equals(this.description) && other.getName().equals(this.getName()) && other.getDiscordURL().equals(this.getDiscordURL());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscordURL() {
        return discordURL;
    }

    public void setDiscordURL(String discordURL) {
        this.discordURL = discordURL;
    }
}
