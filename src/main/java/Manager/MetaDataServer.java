package Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * MetaData Server For Manager
 * Only use by Manager
 */
class MetaDataServer {
    private Integer nbLocalSever;
    private List<String> serverListInfo = null;

    MetaDataServer() {
        this.nbLocalSever = 0;
        this.serverListInfo = new ArrayList<>();
    }

    void addServer(String ip, String port){
        if(ip.equals("auto")) {
            synchronized (nbLocalSever){
                this.nbLocalSever++;
            }
            return;
        }
        this.serverListInfo.add(ip);
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

    List<String> getServerListInfo() {
        return serverListInfo;
    }
}
