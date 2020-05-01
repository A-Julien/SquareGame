package Server;
import Class.Task;
import Client.TaskClient;
import Utils.Communication;
import com.rabbitmq.client.Channel;
import Class.PositionGrille;
import java.io.IOException;

public class TaskServer extends Task implements ServerReaction{
    public TaskServer(String command, String replyQueue) {
        super(command, replyQueue);
    }

    public String handle(Channel channel, Channel fannout) throws IOException {
        switch(type) {
            case 1: // MOOVE
               return playerWantedToMove(channel, fannout);
            case 5: // PRINT
                return print();


            default:
        }
        return "Tache innconue " + this.cmd + " de type " + this.type;
    }

    public void forward(String to, Channel channel){

    }

    boolean dansMazone(){
        return true;
    }

    @Override
    public String playerWantedToMove(Channel channel, Channel fannout) throws IOException {
        if (dansMazone()) {
            // MOVE IN THE GRID
            TaskClient reply2c = new TaskClient("PRINT TU PEUX BOUGER","null");
            TaskServer pingMooveSerer = new TaskServer("NEW_NEIGHBOR NEAR YOU ?", replyQueue);
            channel.basicPublish("", replyQueue, null, Communication.serialize(reply2c));
            fannout.basicPublish("BROADCAST", "", null, Communication.serialize(pingMooveSerer));
            //
            return "Task Handled Correctly";
        } else {
            //forward("QUEUE_SERVEUR_QUI_GERE_ZONE", channel);
            return "task forwarded";
        }
    }

    @Override
    public void checkForNeighbors(PositionGrille p) {

    }

    @Override
    public boolean newPlayer() {
        return true;
    }

    @Override
    public void forwardMessage() {

    }


}
