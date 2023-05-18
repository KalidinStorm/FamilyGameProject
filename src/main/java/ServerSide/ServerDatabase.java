package ServerSide;

import Shared.Game;
import Shared.GameServer;

import java.util.List;

public interface ServerDatabase {
    public List<Game> GetGameList();
    public String GetServer(GameServer gameServer, Game game);
    public boolean DeleteServer(GameServer gameServer);
    public boolean AddServer(String gameTitle, GameServer gameServer, String PathToJar);
    public boolean UpdateServer(GameServer gameServer, Game game);
}
