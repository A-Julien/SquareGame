package Manager.Map;

import java.io.Serializable;
import java.util.ArrayList;

public class Zone implements Serializable {
    int id;
    private String nomZone;
    private String serverQueueName;
    private String ip = "auto";
    private String port = "auto";
    private Color zoneColor = null;
    private ArrayList<Cell> cell;


    public String getServerQueueName() {
        return serverQueueName;
    }

    public void setColor(double red, double green, double blue) {
        if (this.zoneColor == null) {
            this.zoneColor = new Color(red, green, blue);
        }
    }

    public void setServerQueueName(String serverQueueName) {
        this.serverQueueName = serverQueueName;
    }

    public ArrayList<Cell> getCells() {
        return cell;
    }

    public void setCell(ArrayList<Cell> cell) {
        this.cell = cell;
    }

    public Zone(String nom){
        this.nomZone = nom;
        cell = new ArrayList<>();
    }

    public Zone(ZoneFx zone){
        this.nomZone = zone.getNomZone();
        this.id = zone.getId();
        this.cell = zone.getCells();
        this.ip = zone.getIp();
        this.port = zone.getPort();
    }

    public void addCell(Cell c){
        cell.add(c);
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

    public Cell find(Cell cell){
        for (Cell pos : this.cell) if (pos.equals(cell)) return pos;
        return null;
    }

    public Color getNonFxZoneColor() {
        return zoneColor;
    }
}
