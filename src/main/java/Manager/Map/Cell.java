package Manager.Map;

import java.io.Serializable;
import Exception.PositionError;

/**
 * Represent a cell in map
 */
public class Cell implements Serializable {
    private int x;
    private int y;
    private String clientQueue;

    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return x coord
     */
    public int getX() {
        return x;
    }

    /**
     * Set x cood
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return y coord
     */
    public int getY() {
        return y;
    }

    /**
     * set y coord
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "["+getX()+";"+getY()+"]";
    }

    public void plus(Cell p){
        x += p.getX();
        y += p.getY();
    }

    /**
     * Equal between two cell
     * @param obj cell to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return ((Cell) obj).getX() == this.getX() && ((Cell) obj).getY() == this.getY();
    }

    /**
     * Check if they are a client in the case
     * @return true if client in cell, false otherwise
     */
    public boolean isOccupation() {
        return this.clientQueue != null;
    }

    /**
     * Remove client form cell
     *
     * @throws PositionError raise if no client on this cell
     */
    public void clientLeave() throws PositionError {
        if(this.clientQueue != null) {
            this.clientQueue = null;
            return;
        }
        throw new PositionError(
                "Try to remove client at postion " + this.toString() +
                " but no client in this position");
    }

    /**
     * Set client in cell
     * @param clientQueue the client to add
     */
    public void setClient(String clientQueue) {
        this.clientQueue = clientQueue;
    }

    /**
     * Return the client queue associate to the celle
     * @return the client queue or null if no client
     */
    public String getClientQueue() {
        return clientQueue;
    }
}
