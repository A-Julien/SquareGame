package Server;
import Task.Task;

import java.io.IOException;

public interface ServerReaction {
    String playerWantedToMove(Task task) throws IOException;
    //public void checkForNeighbors(PositionGrille p);
    void computeFowardedTask(Task task);
    void forwardMessage();
    String print();
}