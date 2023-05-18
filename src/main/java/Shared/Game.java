package Shared;

import java.io.Serializable;

public class Game implements Serializable {
    private String title;
    private GameServer[] gameServers;

    public Game(String title) {
        this.title = title;
        gameServers = new GameServer[0];
    }

    public void addServer(GameServer gameServer) {
        GameServer[] newGameServers = new GameServer[gameServers.length + 1];
        for (int i = 0; i < gameServers.length; i++) {
            newGameServers[i] = gameServers[i];
        }
        newGameServers[gameServers.length] = gameServer;
        gameServers = newGameServers;
    }

    public void removeServer(GameServer gameServer) {
        int index = -1;
        for (int i = 0; i < gameServers.length; i++) {
            if (gameServers[i].equals(gameServer)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            GameServer[] newGameServers = new GameServer[gameServers.length - 1];
            for (int i = 0, j = 0; i < gameServers.length; i++) {
                if (i != index) {
                    newGameServers[j++] = gameServers[i];
                }
            }
            gameServers = newGameServers;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GameServer[] getServers() {
        return gameServers;
    }

    public void setServers(GameServer[] gameServers) {
        this.gameServers = gameServers;
    }
}