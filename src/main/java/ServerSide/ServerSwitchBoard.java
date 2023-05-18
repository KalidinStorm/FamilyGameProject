package ServerSide;

import Shared.Game;
import Shared.GameServer;
import ServerSide.GameLaunchers.GameLauncher;
import Shared.Requests.StartServerRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ServerSwitchBoard {
    private static ServerSwitchBoard instance = null;
    private GameServer gameServer = null;
    private Game game = null;
    private boolean active = false;
    private Timestamp ts = null;
    private GameLauncher gameLauncher;
    private final ServerDatabase serverDatabase = new InFileServerDatabase();
    private String pathToActiveGame;

    private ServerSwitchBoard(){

    }
    public static ServerSwitchBoard GetInstance() {
        if(instance == null){
            instance = new ServerSwitchBoard();
        }
        return instance;
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public GameLauncher getGameLauncher() {
        return gameLauncher;
    }

    public void setGameLauncher(GameLauncher gameLauncher) {
        this.gameLauncher = gameLauncher;
    }

    public void Launch(StartServerRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {
        if(active){
            if(hasPassed4Hours(ts)){
                StopServer();
            }else{
                return;
            }
        }
        active = true;
        StampTime();
        game = request.getGame();
        gameServer = request.getGameServer();
        String prefix = request.getGame().getTitle();
        Class<?> clazz = Class.forName( "ServerSide.GameLaunchers." + prefix + "Launcher");
        gameLauncher = (GameLauncher) clazz.getDeclaredConstructor().newInstance();
        String path = serverDatabase.GetServer(request.getGameServer(),request.getGame());
        gameLauncher.launchServer(path);
    }

    public static boolean hasPassed4Hours(Timestamp ts) {
        LocalDateTime refTime = ts.toLocalDateTime().plusHours(4);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(refTime);
    }

    private void StampTime(){
        Date date = new Date();
        long time = date.getTime();
        ts = new Timestamp(time);
    }

    public void StopServer(){
        gameLauncher.stopServer();
        serverDatabase.UpdateServer(gameServer,game);
        active = false;
    }

}
