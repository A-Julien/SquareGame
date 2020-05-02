package Server.Sevices;

import Manager.Map.Cell;
import Manager.Map.Zone;
import Exception.ZoneNotFound;
import Exception.*;


import java.util.List;

public class MapService {
    private List<Zone> map;
    private Integer indexZone;

    public MapService(List<Zone> map, Integer zoneID) throws ZoneNotFound {
        this.map = map;
        this.indexZone = this.getIndexZone(zoneID);
    }


    /***
     *
     * init client cell
     *
     * @param clientQueue
     * @return
     * @throws CellNotFound
     */
    public Cell initPositionClient(String clientQueue) throws CellNotFound {
        Cell cell = this.getFreeCell();
        cell.setClient(clientQueue);
        return cell;
    }

    /**
     * Return free cell if exist
     *
     * @return cell
     * @throws CellNotFound if exist no free cell
     */
    private Cell getFreeCell() throws CellNotFound {
        for (Cell cell : this.map.get(this.indexZone).getCells()) if(!cell.isOccupation()) return cell;
        throw new CellNotFound("Can not find free cell");
    }

    /**
     * Return true if the case are in the zone
     *
     * @param pos the position to check
     * @return true if in the server zone
     */
    public boolean isInMyZone(Cell pos) {
        return this.map.get(this.indexZone).find(pos) != null;
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
        throw new ZoneNotFound("Can't find server who manage " +  pos.toString() + " cell");
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
                "Position " +
                pos.toString() +
                " in (zone: " +
                this.map.get(this.indexZone).getNomZone() +
                " of id: "+
                this.map.get(this.indexZone).getId() + ") not free");
    }


    /**
     * Return the position of a client
     *
     * @param clientQueue the client queue
     * @return the position
     * @throws ClientNotFound if the client to found in the server zone, maybe the client are in a other zone
     */
    public Cell getPosClient(String clientQueue) throws ClientNotFound {
        for (Cell pos : this.map.get(this.indexZone).getCells()) if(pos.getClientQueue() != null && pos.getClientQueue().equals(clientQueue)) return pos;

        throw new ClientNotFound(
                "client not found in (zone: " +
                this.map.get(this.indexZone).getNomZone() +
                " of id: "+
                this.map.get(this.indexZone).getId() + ")");
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
    public void moveClient(String clientQueue, Cell newPos) throws PositionError, ClientActionError, ClientNotFound {
        for (Cell cell : this.map.get(this.indexZone).getCells()){
            if(cell.equals(newPos)) {
                if (cell.isOccupation()) throw new ClientActionError(
                        "Cannot perform moving " +
                        clientQueue +
                        " to " + newPos.toString() +
                        " client " + cell.getClientQueue() + " already here");
                (this.getPosClient(clientQueue)).clientLeave();
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
     * Remove a client of a cell
     * This method are used when client move in a other server
     *
     * @param clientQueue the client queue
     * @throws ClientNotFound verify if the client exist
     * @throws PositionError verify if position exist
     */
    public void cleanCell(String clientQueue) throws ClientNotFound, PositionError {

        (this.getPosClient(clientQueue)).clientLeave();
    }

    /**
     * Add a client to a cell
     * This method are used when a client migrate from a other server
     *
     * @param clientQueue the new client queue
     * @param newCell the target cell
     * @throws PositionNotFound verify if the target position exist
     */
    public void setNewClient(String clientQueue, Cell newCell) throws PositionNotFound {
        this.isPosFree(newCell);
        (this.map.get(this.indexZone).find(newCell)).setClient(clientQueue);
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
        throw new ZoneNotFound("can not resolve zone id : " + zoneID );
    }
}
