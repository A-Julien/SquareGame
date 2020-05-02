package Manager;

import java.util.HashMap;

public class MetaDataServer {
    private int nbLocalSever;
    private HashMap<String,String> serverListInfo = null;

    public MetaDataServer() {
        this.nbLocalSever = 0;
        this.serverListInfo = new HashMap<>();
    }

    public void addServer(String ip, String port){
        if(ip.equals("auto")) this.nbLocalSever++;
        this.serverListInfo.put(ip, port);
    }

    public int getNbLocalSever() {
        return nbLocalSever;
    }

    public int getNbServer(){
        return this.serverListInfo.size();
    }

    public HashMap<String, String> getServerListInfo() {
        return serverListInfo;
    }
}
