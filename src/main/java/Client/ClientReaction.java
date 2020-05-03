package Client;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public interface ClientReaction {
    public String reactToPong();
    public String reactToPing(Channel channel) throws IOException;
    public String sayHelloToFriend(Channel channel, String myQueue) throws IOException;
}

