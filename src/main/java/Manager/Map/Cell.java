package Manager.Map;

import java.io.Serializable;
import Exception.PositionError;

public class Cell implements Serializable {
    private int x;
    private int y;
    private String clientQueue;

    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

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

    @Override
    public boolean equals(Object obj) {
        return ((Cell) obj).getX() == this.getX() && ((Cell) obj).getY() == this.getY();
    }

    public boolean isOccupation() {
        return this.clientQueue != null;
    }

    public void clientLeave() throws PositionError {
        if(this.clientQueue != null) {
            this.clientQueue = null;
            return;
        }
        throw new PositionError(
                "Try to remove client at postion " + this.toString() +
                " but no client in this position");
    }

    public void setClient(String clientQueue) {
        this.clientQueue = clientQueue;
    }

    public String getClientQueue() {
        return clientQueue;
    }
}
