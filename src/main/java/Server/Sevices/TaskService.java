package Server.Sevices;
import Manager.Map.Cell;
import Manager.Map.Zone;

import Server.ServerReaction;
import Task.Task;
import Task.TaskCommand;
import Task.Deplacement;

import Utils.Communication;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.List;

import Exception.*;

public class TaskService implements ServerReaction {
    private Channel outChannel;
    private Channel sendBroadcastChanel;
    private MapService mapService;
    private String myReponsseQueue;

    public TaskService(Channel outChannel, Channel sendBroadcastChanel, MapService mapService, String myResponsseQueue) {
        this.outChannel = outChannel;
        this.sendBroadcastChanel = sendBroadcastChanel;
        this.mapService = mapService;
        this.myReponsseQueue = myResponsseQueue;
    }

    public String compute(Task task) throws IOException {
        switch(task.cmdType) {
            case MOVE:
                this.playerWantedToMove(task);
                break;
            case PRINT:

                break;
            case FORWARD_MESSAGE:
                computeFowardedTask(task);
                break;
            case FORWARD_APPROVED:
                try {
                    mapService.cleanCell( (String) task.cmd);

                } catch (ClientNotFound | PositionError err) {
                    err.printStackTrace();

                }
                break;
            case NEIHGBOR:
                mayNeighbor(task);
            default:
        }
        return "Tache innconue "; //+ this.cmd + " de type " + this.type;
    }

    public void forward(String to, Channel channel){

    }

    boolean isInZone(){
        return true;
    }

    /**
     * A client want to moove from is potision to either left, up , rigth, down resp (-1;0) (0,-1) (1,0) (0,1)
     * @param task
     * @return
     * @throws IOException
     */
    @Override
    public void playerWantedToMove(Task task) throws IOException {
        Task taskToSend;


        try{
            // Retrieve client position
            System.out.println(task.replyQueu);
            Cell actualPosition = this.mapService.getPosClient(task.replyQueu);
            int dX = 0;
            int dY = 0;
            switch((Deplacement) task.cmd){
                case BAS:
                    dY = 1;
                    break;
                case HAUT:
                    dY = -1;
                    break;
                case GAUCHE:
                    dX = -1;
                    break;
                case DROITE:
                    dX = 1;
            }
            Cell target = new Cell( actualPosition.getX() + dX, actualPosition.getY() + dY);

           if(mapService.isInMyZone(target)){
             // The destination is in the current handled zone

               try {

                   mapService.isPosFree(target);
                   mapService.moveClient(task.replyQueu, target);

                   taskToSend = new Task(TaskCommand.MOVE_GRANTED,target, null);
                   checkForNeighbor( target, task.replyQueu);

               } catch (PositionNotFound | PositionError | ClientActionError positionNotFound) {
                   positionNotFound.printStackTrace();
                   taskToSend = new Task(TaskCommand.MOVE_NOT_GRANTED, null , null);

               }

                 this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(taskToSend));
                return;
           }
            try {

                String queueServerHandlingCible = mapService.whoManageCell(target);
                task.cmd = target;

                taskToSend = new Task(TaskCommand.FORWARD_MESSAGE, task, myReponsseQueue );
                this.outChannel.basicPublish("",queueServerHandlingCible, null, Communication.serialize(taskToSend));

            } catch (ZoneNotFound zoneNotFound) {
                System.out.println("Error : Cell out of bounds");
                taskToSend = new Task(TaskCommand.MOVE_NOT_GRANTED, task, myReponsseQueue );
                this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(taskToSend));
            }


        } catch ( ClientNotFound err){
            System.out.println("Error : " + err.toString());
            taskToSend = new Task(TaskCommand.CLIENT_NOT_FOUNDED, null , null);
             this.outChannel.basicPublish("",task.replyQueu, null, null);
        }






    }

    /**
     * Get a task forwarded from an other server, if not possible do not anser to server but only to client
     * If possible, send to server to forget the client and to client the aprobation
     * @param task
     * @throws IOException
     */
    @Override
    public void computeFowardedTask(Task task) throws IOException {
        String serveurHandlingClient = task.replyQueu;
        Task  sendedFromClient = (Task) task.cmd;
        String client = sendedFromClient.replyQueu;
        Cell target = (Cell) sendedFromClient.cmd;
        try {
            if(!mapService.isPosFree(target)){

                // AJOUTER CLIENTS A LA TARGET
                this.mapService.setNewClient(client, target);

                // SERVER FORGET CLIENT
                Task taskToSendToServer = new Task(TaskCommand.FORWARD_APPROVED, client , null);
                this.outChannel.basicPublish("",serveurHandlingClient, null,  Communication.serialize(taskToSendToServer));

                // CLIENT MOOVE AND NEW SERVER
                Task taskToSendToCLient = new Task(TaskCommand.MOVE_GRANTED_FORWARDED, target , myReponsseQueue);
                this.outChannel.basicPublish("",client, null,  Communication.serialize(taskToSendToCLient));

                checkForNeighbor( target, client);



            }





        } catch (PositionNotFound positionNotFound) {
            positionNotFound.printStackTrace();
            Task taskToSendToCLient = new Task(TaskCommand.MOVE_NOT_GRANTED, null , null);
            this.outChannel.basicPublish("",client, null,  Communication.serialize(taskToSendToCLient));


        }

    }

    /**
     * Contact the servers that handle the cells next to this one for saying hello to client
     * @param c
     */
    @Override
    public void checkForNeighbor(Cell c, String queueClient) {
        Cell voisine;
        Task contactServerNeighbor;

        try {
            voisine = new Cell(c.getX()+1, c.getY());
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, null , queueClient);
            this.outChannel.basicPublish("",mapService.whoManageCell(voisine), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            System.out.println("Cell out of bounds");
        }

        try {
            voisine = new Cell(c.getX()-1, c.getY());
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, null , queueClient);
            this.outChannel.basicPublish("",mapService.whoManageCell(voisine), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            System.out.println("Cell out of bounds");
        }

        try {
            voisine = new Cell(c.getX(), c.getY()+1);
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, null , queueClient);
            this.outChannel.basicPublish("",mapService.whoManageCell(voisine), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            System.out.println("Cell out of bounds");
        }

        try {
            voisine = new Cell(c.getX()+1, c.getY()-1);
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, null , queueClient);
            this.outChannel.basicPublish("",mapService.whoManageCell(voisine), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            System.out.println("Cell out of bounds");
        }


    }

    @Override
    public void ayNeighbor(Task task) {
        //mapService.
    }


    @Override
    public void forwardMessage() {

    }

    @Override
    public String print() {
        return null;
    }




}
