package ServerSide.GameLaunchers;

import java.io.File;
import java.io.IOException;

public class ValheimLauncher implements GameLauncher {
    private Process process;

    @Override
    public void launchServer(String path) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "valheim_server.x86_64.exe", "-nographics", "-batchmode");
            processBuilder.directory(new File(path));
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() {
        if (process != null) {
            process.destroy();
        }
    }
}
