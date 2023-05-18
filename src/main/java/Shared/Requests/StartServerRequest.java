package Shared.Requests;

import Shared.Game;
import Shared.GameServer;

public class StartServerRequest {
    public Game game;
    public GameServer gameServer;
    private StartServerRequest(){}
    public StartServerRequest(Game game, GameServer gameServer) {
        this.game = game;
        this.gameServer = gameServer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }
}
