package ServerSide;


import Shared.Game;
import Shared.GameServer;
import Shared.JsonSerializer;
import Shared.Serializer;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InFileServerDatabase implements ServerDatabase {

    Serializer serializer = new JsonSerializer();

    @Override
    public List<Game> GetGameList() {
        return GetGameServerManifest();
    }

    @Override
    public String GetServer(GameServer gameServer, Game game) {
        String pathToStorage = ServerUtils.pathToDataBase
                + "/" + game.getTitle()
                + "/" + gameServer.getName();
        return pathToStorage;
    }

    @Override
    public boolean DeleteServer(GameServer gameServer) {
        return false;
    }

    @Override
    public boolean AddServer(String gameTitle, GameServer gameServer, String PathToJar) {
        AddToGameAndServerList(gameTitle, gameServer);
        AddToGameServers(gameTitle, gameServer, PathToJar);
        return false;
    }
    @Override
    public boolean UpdateServer(GameServer gameServer, Game game) {
        return false;
    }

    private boolean AddToGameServers(String gameTitle, GameServer gameServer, String PathToJar) {
        try {
            String path = "src/main/java/ServerSide/Database/" + gameTitle + "/" + gameServer.getName();
            File file = new File(path);
            if (!file.exists()) {
                if (file.mkdir()) {
                    System.out.println("Folder created successfully");
                    MoveJar(PathToJar, path);
                } else {
                    System.out.println("Failed to create folder");
                }
            } else {
                System.out.println("Folder already exists");
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private boolean AddToGameAndServerList(String gameTitle, GameServer gameServer) {
        List<Game> manifest = GetGameServerManifest();
        for(Game game : manifest){
            if(game.getTitle().equals(gameTitle)){
                game.addServer(gameServer);
                return ReplaceGameManifest(manifest);
            }
        }
        return false;
    }

    private boolean ReplaceGameManifest(List<Game> manifest){
        try {
            String content = serializer.serialize(manifest);
            PrintWriter writer = new PrintWriter("src/main/java/ServerSide/Database/GameServerManifest.txt");
            writer.print(content);
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private List<Game> GetGameServerManifest(){
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/java/ServerSide/Database/GameServerManifest.txt")));
            Type gameListType = new TypeToken<List<Game>>() {}.getType();
            return serializer.deserialize(content,gameListType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void MoveJar(String source, String Dest) throws IOException {
        File sourceFile = new File(source);
        Path destinationPath = Paths.get(Dest);
        Files.createDirectories(destinationPath);
        Files.move(sourceFile.toPath(), destinationPath.resolve(sourceFile.getName()));
    }


}
