package Manager.Map;

import FX.Map.PositionGrille;
import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Zone implements Serializable {
    private static int compteur_id = 0;
    int id;
    private String nomZone;
    private String serverQueueName;
    private String ip = "auto";
    private String port = "auto";
    private ArrayList<PositionGrille> positionGrille;


    public String getServerQueueName() {
        return serverQueueName;
    }

    public void setServerQueueName(String serverQueueName) {
        this.serverQueueName = serverQueueName;
    }

    public ArrayList<PositionGrille> getPositionGrille() {
        return positionGrille;
    }

    public void setPositionGrille(ArrayList<PositionGrille> positionGrille) {
        this.positionGrille = positionGrille;
    }

    public Zone(String nom, Color c){
        this.nomZone = nom;
        this.id = compteur_id;
        compteur_id++;
        positionGrille = new ArrayList<>();
    }

    public Zone(ZoneFx zone){
        this.nomZone = zone.getNomZone();
        this.id = compteur_id;
        compteur_id++;
        this.positionGrille = zone.getPositionGrille();
        this.ip = zone.getIp();
        this.port = zone.getPort();
    }

    public void addCell(PositionGrille c){
        positionGrille.add(c);
    }

    public String getNomZone() {
        return nomZone;
    }

    public void setNomZone(String nomZone) {
        this.nomZone = nomZone;
    }


    @Override
    public String toString() {
        return "Zone : "+nomZone+ " ip = " + ip + " port = " + port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


}
