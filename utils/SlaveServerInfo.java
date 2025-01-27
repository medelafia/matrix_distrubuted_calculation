package utils;

import slaves.SlaveServer;

public class SlaveServerInfo {
    private String hostName ;
    private int port ;

    public SlaveServerInfo(String hostName , int port ) {
        this.hostName = hostName ;
        this.port = port ;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }


}
