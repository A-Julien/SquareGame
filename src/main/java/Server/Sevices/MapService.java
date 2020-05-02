package Server.Sevices;

import FX.Client;
import Manager.Map.Cell;
import Manager.Map.Zone;
import Exception.ZoneNotFound;
import Exception.PositionNotFound;
import Exception.ClientNotFound;
import Exception.PositionError;
import Exception.ClientActionError;

import java.util.List;

public class MapService {
    private List<Zone> map;
    private Integer indexZone;

    public MapService(List<Zone> map, Integer zoneID) throws ZoneNotFound {
        this.map = map;
        this.indexZone = this.getIndexZone(zoneID);
    }

    /**
     * Return true if the case are in the zone
     *
     * @param pos the position to check
     * @return true if in the server zone
     */
    public boolean isInMyZone(Cell pos) {
        for (Zone zone : this.map) {
            if (zone.find(pos) != null) return true;
        }
        return false;
    }

    /**
     * Return the Server queue name of a position
     *
     * @param pos the position
     * @return Server queue name
     * @throws ZoneNotFound if server not found
     */
    public String whoManageCell(Cell pos) throws ZoneNotFound {
        for (Zone zone : this.map) {
            if (zone.find(pos) != null) return zone.getServerQueueName();
        }
        throw new ZoneNotFound("[MAPSERVICE] Can't find zone with pos : " + pos.toString());
    }


    /**
     * Check if the position given in param is free in the server zone
     *
     * free = no client at this position
     * @param pos the position
     * @return true if free, false otherwise
     * @throws PositionNotFound  if the position not exist, maybe the cell are in a other zone
     */
    public boolean isPosFree(Cell pos) throws PositionNotFound {
        for (Cell cell : this.map.get(this.indexZone).getCells()) if (cell.equals(pos)) return cell.isOccupation();

        throw new PositionNotFound(
                "[MANAGER] can't find position " +
                pos.toString() +
                "[zone: " +
                this.map.get(this.indexZone).getNomZone() +
                ",id: "+
                this.map.get(this.indexZone).getId());
    }


    /**
     * Return the position of a client
     *
     * @param clientQueue the client queue
     * @return the position
     * @throws ClientNotFound if the client to found in the server zone, maybe the client are in a other zone
     */
    public Cell getPosClient(String clientQueue) throws ClientNotFound {
        for (Cell pos : this.map.get(this.indexZone).getCells()){
            if(pos.getClientQueue().equals(clientQueue)) return pos;
        }
        throw new ClientNotFound(
                "client not found in [zone: " +
                this.map.get(this.indexZone).getNomZone() +
                ",id: "+
                this.map.get(this.indexZone).getId());
    }


    /**
     * Move client in a other position.
     * This method is call by server.
     *
     * @param clientQueue the client who want move
     * @param newPos the new position of the client
     *
     * @throws PositionError if position do not exit
     * @throws ClientActionError if the cell are not free
     */
    public void moveClient(String clientQueue, Cell newPos) throws PositionError, ClientActionError {
        for (Cell cell : this.map.get(this.indexZone).getCells()){
            if(cell.equals(newPos)) {
                if (cell.isOccupation()) throw new ClientActionError(
                        "Cannot perform moving " +
                        clientQueue +
                        "to " + newPos.toString() +
                        "client " + cell.getClientQueue() + " already here");
                cell.clientLeave();
                cell.setClient(clientQueue);
                return;
            }
        }
        throw new ClientActionError(
                "Cannot perform moving " +
                clientQueue +
                "to " +
                newPos.toString());
    }


    /**
     * internal method
     *
     * @param zoneID
     * @return
     * @throws ZoneNotFound
     */
    private Integer getIndexZone(Integer zoneID) throws ZoneNotFound {
        int index = 0;
        for (Zone zone : this.map) {
            if (zone.getId() == zoneID) return index;
            index++;
        }
        throw new ZoneNotFound("[MAPSERVICE] can not resolve zone name");
    }
}
