package Shared.Responses;

import java.sql.Timestamp;

public class ActiveServerResponse {
    private boolean active;
    private Timestamp ts;
    private String serverName;
    private String gameName;
    private String ip;

    public ActiveServerResponse(boolean active, Timestamp ts, String serverName, String gameName, String ip) {
        this.active = active;
        if(!active){
            this.ts = new Timestamp(0);
            this.serverName = "";
            this.gameName = "";
            this.ip = "";
        }else{
            this.ts = ts;
            this.serverName = serverName;
            this.gameName = gameName;
            this.ip = ip;
        }
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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
