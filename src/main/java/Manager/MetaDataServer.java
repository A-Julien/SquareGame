package Manager;

import java.util.HashMap;

/**
 * MetaData Server For Manager
 * Only use by Manager
 */
class MetaDataServer {
    private Integer nbLocalSever;
    private HashMap<String,String> serverListInfo = null;

    MetaDataServer() {
        this.nbLocalSever = 0;
        this.serverListInfo = new HashMap<>();
    }

    void addServer(String ip, String port){
        if(ip.equals("auto")) {
            synchronized (nbLocalSever){
                this.nbLocalSever++;
            }
            return;
        }
        this.serverListInfo.put(ip, port);
    }

    int getNbLocalSever() {
        return nbLocalSever;
    }

    int getExternalServer(){
        return this.serverListInfo.size();
    }

    int getNbServer(){
        return this.serverListInfo.size() + this.nbLocalSever;
    }

    HashMap<String, String> getServerListInfo() {
        return serverListInfo;
    }
}
