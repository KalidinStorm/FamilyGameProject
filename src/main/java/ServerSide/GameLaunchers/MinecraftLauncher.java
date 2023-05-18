package ServerSide.GameLaunchers;

import ServerSide.GameLaunchers.GameLauncher;
import ServerSide.ServerUtils;

import java.io.*;
import java.net.InetAddress;

public class MinecraftLauncher implements GameLauncher {

    private static Process process;

    @Override
    public void launchServer(String path) {
        try {
            // Change the path to the location of the Minecraft server jar file
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "server.jar");
            pb.directory(new File(path));
            //pb.inheritIO();
            process = pb.start();
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    @Override
    public void stopServer() {
        if (process != null) {
            try {
                process.getOutputStream().write("stop".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            process.destroy();
        }
    }
}
