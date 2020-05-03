package Server.Sevices;
import Task.Task;
import java.io.IOException;
import Exception.*;
import Manager.Map.Cell;

public interface TaskServiceReaction {
    void playerWantedToMove(Task task) throws IOException;
    void playerWantColorZone(Task task) throws IOException;
    //public void checkForNeighbors(Cell p);
    void computeFowardedTask(Task task) throws PositionNotFound, IOException;
    void checkForNeighbor(Cell c, String queueClient);
    void mayNeighbor(Task task) throws IOException;
    String print();
}