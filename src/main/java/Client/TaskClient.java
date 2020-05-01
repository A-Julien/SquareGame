package Client;
import Class.Task;
import Utils.Communication;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TaskClient extends Task implements ClientReaction {
    public TaskClient(String command, String replyQueue) {

        super(command, replyQueue);
    }


    public String  handle(Channel channel, String myQueue) throws IOException {
        switch(type){
            case 4: // PING A FRIEND
                sayHelloToFriend(channel,myQueue);
            case 5: // PRINT
                return this.print();
            case 6: //PING
                return reactToPing(channel);
            case 7: // PONG
                return reactToPong();


        }
        return "Tache innconue " + this.cmd + " de type " + this.type;
    }


    @Override
    public String sayHelloToFriend(Channel channel, String myQueue) throws IOException {
        TaskClient t = new TaskClient("PING ", myQueue);
        channel.basicPublish("", replyQueue, null, Communication.serialize(t));
        return null;
    }

    @Override
    public String reactToPing(Channel channel) throws IOException {
        String responsse = "PRINT Bonjour 0 toi qussi !";
        TaskClient t = new TaskClient("PONG ", null);
        channel.basicPublish("", replyQueue, null, Communication.serialize(t));
        return "J'ai répondu au voisin";
    }

    @Override
    public String reactToPong(){
        return "Le voisin m'as répondu";
    }


}
