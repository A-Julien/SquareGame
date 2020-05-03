package Server.Sevices;

import Manager.Map.Cell;
import Manager.Map.Color;
import Exception.*;

public interface MapServiceInterface {

    String isSomeOneWhere(Cell cell);

    /**
     * Return the server zone color
     *
     * @return the server zone color
     */
    Color getZoneColor();

    /***
     *
     * init client cell
     *
     * @param clientQueue
     * @return
     * @throws CellNotFound
     */
    Cell initPositionClient(String clientQueue) throws CellNotFound;

    /**
     * Return free cell if exist
     *
     * @return cell
     * @throws CellNotFound if exist no free cell
     */
    Cell getFreeCell() throws CellNotFound ;

    /**
     * Return true if the case are in the zone
     *
     * @param pos the position to check
     * @return true if in the server zone
     */
    boolean isInMyZone(Cell pos);

    /**
     * Return the Server queue name of a position
     *
     * @param pos the position
     * @return Server queue name
     * @throws ZoneNotFound if server not found
     */
    String whoManageCell(Cell pos) throws ZoneNotFound ;

    /**
     * Check if the position given in param is free in the server zone
     *
     * free = no client at this position
     * @param pos the position
     * @return true if free, false otherwise
     * @throws PositionNotFound  if the position not exist, maybe the cell are in a other zone
     */
    boolean isPosFree(Cell pos) throws PositionNotFound ;

    /**
     * Return the position of a client
     *
     * @param clientQueue the client queue
     * @return the position
     * @throws ClientNotFound if the client to found in the server zone, maybe the client are in a other zone
     */
    Cell getPosClient(String clientQueue) throws ClientNotFound ;
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
    void moveClient(String clientQueue, Cell newPos) throws PositionError, ClientActionError, ClientNotFound;


    /**
     * Remove a client of a cell
     * This method are used when client move in a other server
     *
     * @param clientQueue the client queue
     * @throws ClientNotFound verify if the client exist
     * @throws PositionError verify if position exist
     */
    void cleanCell(String clientQueue) throws ClientNotFound, PositionError;

    /**
     * Add a client to a cell
     * This method are used when a client migrate from a other server
     *
     * @param clientQueue the new client queue
     * @param newCell the target cell
     * @throws PositionNotFound verify if the target position exist
     */
    void setNewClient(String clientQueue, Cell newCell) throws PositionNotFound, ZoneNotFound;


}
