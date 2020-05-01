package Server;
import Class.PositionGrille;
import FX.Case;
import Class.Player;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.ArrayList;

public interface ServerReaction {
    public String playerWantedToMove(Channel clientQueue, Channel fannout) throws IOException;
    public void checkForNeighbors(PositionGrille p);
    public boolean newPlayer();
    public void forwardMessage();
    public String print();
}

