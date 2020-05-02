package Server;
import FX.Map.PositionGrille;
import Manager.Map.Zone;
import Task.Task;

import Utils.Communication;
import com.rabbitmq.client.Channel;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.List;


public class ComputeTaskServer implements ServerReaction{
    private Channel outChannel;
    private Channel sendBroadcastChanel;
    private List<Zone> map;

    public ComputeTaskServer(Channel outChannel, Channel sendBroadcastChanel,  List<Zone> map) {
        this.outChannel = outChannel;
        this.sendBroadcastChanel = sendBroadcastChanel;
        this.map = map;
    }

    public String compute(Task task) throws IOException {
        switch(task.cmdType) {
            case MOVE:
                this.playerWantedToMove(task);
                   // return playerWantedToMove(channel, fannout);
                break;
            case PRINT:
                break;
            default:
        }
        return "Tache innconue "; //+ this.cmd + " de type " + this.type;
    }

    public void forward(String to, Channel channel){

    }

    boolean isInZone(){
        return true;
    }

    @Override
    public String playerWantedToMove(Task task) throws IOException {
        //String[] cmd = ((String)task.cmd).trim().split("\\s+");

        if (isInZone()) {
            // MOVE IN THE GRID
            //TODO COMPUTE
            //this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(TASKCLIENT));
            //fannout.basicPublish("BROADCAST", "", null, Communication.serialize(pingMooveSerer));

            return "Task Handled Correctly";
        } else {
            //forward("QUEUE_SERVEUR_QUI_GERE_ZONE", channel);
            return "task forwarded";
        }
    }

    @Override
    public void computeFowardedTask(Task task) {

    }

    /*@Override
    public void checkForNeighbors(PositionGrille p) {
    }*/

   /* @Override
    public boolean newPlayer() {
        return true;
    }*/

    @Override
    public void forwardMessage() {

    }

    @Override
    public String print() {
        return null;
    }


}
