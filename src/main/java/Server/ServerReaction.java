package Server;
import Task.Task;

import java.io.IOException;
import Exception.*;
import Manager.Map.Cell;
public interface ServerReaction {
    void playerWantedToMove(Task task) throws IOException;
    //public void checkForNeighbors(Cell p);
    void computeFowardedTask(Task task) throws PositionNotFound, IOException;
    void checkForNeighbor(Cell c, String queueClient);

    void  ayNeighbor(Task task);
    String print();
}