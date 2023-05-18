package Shared.Responses;

import Shared.Game;

import java.util.List;

public class GetGameListResponse {
    List<Game> gameList;

    public GetGameListResponse(List<Game> gameList) {
        this.gameList = gameList;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }
}
