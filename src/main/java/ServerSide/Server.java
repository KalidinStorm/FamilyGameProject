package ServerSide;

import Shared.JsonSerializer;
import Shared.Requests.StartServerRequest;
import Shared.Responses.ActiveServerResponse;
import Shared.Responses.GetGameListResponse;
import Shared.Serializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerDatabase database = new InFileServerDatabase();
        Serializer serializer = new JsonSerializer();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/GetGameList", new GetGameListHandler(database,serializer));
        server.createContext("/StartServer", new StartServerHandler(database,serializer));
        server.createContext("/GetActiveServer", new GetActiveServerHandler(database,serializer));
        server.createContext("/StopServer", new StopServerHandler(database,serializer));
        server.createContext("/UploadServer", new UploadServerHandler(database,serializer));
        server.setExecutor(null);
        server.start();
    }
}

class GetActiveServerHandler implements HttpHandler{
    ServerDatabase database;
    Serializer serializer;
    public GetActiveServerHandler(ServerDatabase database, Serializer serializer) {
        this.database = database;
        this.serializer = serializer;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = Processes();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String Processes() throws UnknownHostException {
        ServerSwitchBoard ssb = ServerSwitchBoard.GetInstance();
        if(ssb.isActive()){
            return serializer.serialize(new ActiveServerResponse(ssb.isActive(),ssb.getTs(),ssb.getGameServer().getName(),ssb.getGame().getTitle(),InetAddress.getLocalHost().getHostAddress()));
        }else{
            return serializer.serialize(new ActiveServerResponse(ssb.isActive(),ssb.getTs(),"","",""));
        }
    }

}

class GetGameListHandler implements HttpHandler{
    ServerDatabase database;
    Serializer serializer;
    public GetGameListHandler(ServerDatabase database, Serializer serializer) {
        this.database = database;
        this.serializer = serializer;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = Processes();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String Processes(){
        return serializer.serialize(new GetGameListResponse(database.GetGameList()));
    }

}
class StartServerHandler implements HttpHandler{
    ServerDatabase database;
    Serializer serializer;
    public StartServerHandler(ServerDatabase database, Serializer serializer) {
        this.database = database;
        this.serializer = serializer;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = "";
        int code = 200;
        try {
            Processes(t);
        } catch (Exception e) {
            response = e.getClass().getSimpleName() + ": " + e.getMessage();
            code = 400;
        }
        t.sendResponseHeaders(code, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void Processes(HttpExchange t) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        StartServerRequest request = serializer.deserialize(ServerUtils.getRequestBody(t),StartServerRequest.class);
        ServerSwitchBoard serverSwitchBoard = ServerSwitchBoard.GetInstance();
        serverSwitchBoard.Launch(request);
    }
}
class StopServerHandler implements HttpHandler{
    ServerDatabase database;
    Serializer serializer;
    public StopServerHandler(ServerDatabase database, Serializer serializer) {
        this.database = database;
        this.serializer = serializer;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = "";
        Processes();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void Processes(){
        ServerSwitchBoard ssb = ServerSwitchBoard.GetInstance();
        ssb.StopServer();
    }

}
class UploadServerHandler implements HttpHandler{
    ServerDatabase database;
    Serializer serializer;
    public UploadServerHandler(ServerDatabase database, Serializer serializer) {
        this.database = database;
        this.serializer = serializer;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = "ProcessesRequest.Processes(t)";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

