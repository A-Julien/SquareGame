package Client;
import Task.Task;
import Utils.Direction;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public interface ClientReaction {
     void reactToPong();
     void reactToPing(Task task) throws IOException;
     void changeColor(Task task);
     void handleMouvement(Direction mouvement);
}

