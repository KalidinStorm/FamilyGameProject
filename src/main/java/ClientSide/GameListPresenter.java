package ClientSide;

import Shared.Game;
import Shared.GameServer;
import Shared.JsonSerializer;
import Shared.Requests.StartServerRequest;
import Shared.Responses.ActiveServerResponse;
import Shared.Responses.GetGameListResponse;
import Shared.Responses.StartServerResponse;
import Shared.Responses.StopServerResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameListPresenter {

    private View view;
    Game game;
    GameServer gameServer;
    private final ServerConnector sc = new ServerConnector(new JsonSerializer());

    public interface View{
        void PopulateActiveServerInfo(ActiveServerResponse response);
        void PopulateGameSelection(List<Game> games);
        void PopulateServerSelection(List<GameServer> gameServers);
        void PopulateServerDetails(GameServer gameServer);
        void SetActiveServerInfo();

    }
    public GameListPresenter(GameListPresenter.View view){
        this.view = view;
    }
    public void PopulateActiveServerInfo() {
        try {
            ActiveServerResponse response = sc.doPost("/GetActiveServer",null,null, ActiveServerResponse.class);
            view.PopulateActiveServerInfo(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void PopulateGameList(){
        try {
            List<Game> games;
            games = sc.doPost("/GetGameList",null,null, GetGameListResponse.class).getGameList();
            view.PopulateGameSelection(games);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void PopulateServerSelection(Game game){
        this.game = game;
        List<GameServer> gameServers = Arrays.asList(game.getServers());
        view.PopulateServerSelection(gameServers);
    }

    public void PopulateServerDetails(GameServer gameServer){
        this.gameServer = gameServer;
        view.PopulateServerDetails(gameServer);
    }

    public void StartServer(){
        try {
            StartServerResponse response = sc.doPost("/StartServer",new StartServerRequest(game,gameServer),null, StartServerResponse.class);
            view.SetActiveServerInfo();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void StopServer(){
        try {
            sc.doPost("/StopServer",null,null, StopServerResponse.class);
            view.SetActiveServerInfo();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
