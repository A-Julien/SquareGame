package Server;
import Manager.Map.Cell;
import Manager.Map.Zone;
import Task.Task;
import Task.TaskCommand;

import Utils.Communication;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.List;


public class ComputeTaskServer implements ServerReaction{
    private Channel outChannel;
    private Channel sendBroadcastChanel;
    private List<Zone> map;
    private String myReponsseQueue;

    public ComputeTaskServer(Channel outChannel, Channel sendBroadcastChanel,  List<Zone> map, String myResponsseQueue) {
        this.outChannel = outChannel;
        this.sendBroadcastChanel = sendBroadcastChanel;
        this.map = map;
        this.myReponsseQueue = myResponsseQueue;
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
        Task TaskToSend;
        String[] cmd = ((String)task.cmd).trim().split("\\s+");
        //MapComputer.getCoord(Replyqueue);
        Cell posActuel = new Cell(0,0);
        Cell cible = new Cell(posActuel.getX() + Integer.parseInt(cmd[0]),
                posActuel.getY() + Integer.parseInt(cmd[1]) );
        if (isInZone()) {
            // if(MapManager.isFree(Cell p)){
             if(true){
                 // Swap Joeur Case X,Y
                 TaskToSend = new Task(TaskCommand.MOVE_GRANTED, new String(cible.getX() + " " + cible.getY()), null );
            } else {
                 TaskToSend = new Task(TaskCommand.MOVE_NOT_GRANTED,null, null);
            }

            this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(TaskToSend));
            //fannout.basicPublish("BROADCAST", "", null, Communication.serialize(pingMooveSerer));

            return "Task Handled Correctly";
        } else {
            TaskToSend = new Task(TaskCommand.FORWARD_MESSAGE, task, myReponsseQueue );
          //  this.outChannel.basicPublish("",ReplyQueueForward, null, Communication.serialize(TaskToSend));
            return "task forwarded";
        }
    }

    @Override
    public void computeFowardedTask(Task task) {

    }

    /*@Override
    public void checkForNeighbors(Cell p) {
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
