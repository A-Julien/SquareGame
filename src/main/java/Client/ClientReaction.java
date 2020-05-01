package Client;
import Class.PositionGrille;
import FX.Case;
import Class.Player;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.ArrayList;

public interface ClientReaction {
    public String reactToPong();
    public String reactToPing(Channel channel) throws IOException;
    public String sayHelloToFriend(Channel channel, String myQueue) throws IOException;
}

