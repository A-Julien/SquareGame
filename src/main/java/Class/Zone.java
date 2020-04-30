package Class;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Zone implements Serializable {
    private static int compteur_id = 0;
    int id;
    public String nomZone;
    String ip = "auto";
    String port = "auto";
    ArrayList<PositionGrille> positionGrille;




    public Zone(String nom, Color c){
        this.nomZone = nom;
        this.id = compteur_id;
        compteur_id++;
        positionGrille = new ArrayList<>();
    }

    public Zone(ZoneFx zone){
        this.nomZone = zone.nomZone;
        this.id = compteur_id;
        compteur_id++;
        this.positionGrille = zone.positionGrille;
        this.ip = zone.ip;
        this.port = zone.port;
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
