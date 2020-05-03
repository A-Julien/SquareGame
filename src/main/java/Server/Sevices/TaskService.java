package Server.Sevices;
import Manager.Map.Cell;

import Task.Task;
import Task.TaskCommand;
import Utils.Direction;

import Utils.Communication;
import Utils.Logger.SimpleLogger;
import com.rabbitmq.client.Channel;

import java.io.IOException;

import Exception.*;

public class TaskService implements TaskServiceReaction {
    private Channel outChannel;
    private MapService mapService;
    private String myReponsseQueue;
    private SimpleLogger logger;

    public TaskService(Channel outChannel, MapService mapService, String myResponsseQueue,SimpleLogger logger) {
        this.outChannel = outChannel;
        this.mapService = mapService;
        this.myReponsseQueue = myResponsseQueue;
        this.logger = logger;
        this.logger.addTag("TASK_SERVICE");
    }

    public String compute(Task task) throws IOException, UnknownCmd {
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
                break;
            case GET_COLOR:
                this.playerWantColorZone(task);
                break;
            default:
                throw new UnknownCmd(task.cmd + " not a valid server command");
        }
        return "Tache innconue "; //+ this.cmd + " de type " + this.type;
    }


    /**
     * Send color to client
     *
     * @param task the task to compute
     * @throws IOException serialisation error
     */
    @Override
    public void playerWantColorZone(Task task) throws IOException {
        this.outChannel.basicPublish(
                "",
                task.replyQueu,
                null,
                Communication.serialize(
                        new Task(
                                TaskCommand.GET_COLOR,
                                this.mapService.getZoneColor(),
                                null
                        )
                )
        );// même nachos est plus lisible !!
    }


    /**
     * A client want to move from is potision to either left, up , rigth, down resp (-1;0) (0,-1) (1,0) (0,1)
     * @param task
     * @return
     * @throws IOException
     */
    @Override
    public void playerWantedToMove(Task task) throws IOException {
        Task taskToSend;

        try{
            // Retrieve client position
            Cell actualPosition = this.mapService.getPosClient(task.replyQueu);
            int dX = 0;
            int dY = 0;
            switch((Direction) task.cmd){
                case DOWN:
                    dY = 1;
                    break;
                case UP:
                    dY = -1;
                    break;
                case LEFT:
                    dX = -1;
                    break;
                case RIGHT:
                    dX = 1;
            }
            Cell target = new Cell( actualPosition.getX() + dX, actualPosition.getY() + dY);

           if(this.mapService.isInMyZone(target)){
             // The destination is in the current handled zone
               try {

                   this.mapService.isPosFree(target);
                   this.mapService.moveClient(task.replyQueu, target);

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

                String queueServerHandlingCible = this.mapService.whoManageCell(target);
                task.cmd = target;

                taskToSend = new Task(TaskCommand.FORWARD_MESSAGE, task, myReponsseQueue );
                this.outChannel.basicPublish("",queueServerHandlingCible, null, Communication.serialize(taskToSend));

            } catch (ZoneNotFound zoneNotFound) {
                this.logger.log("Error : Cell out of bounds");
                taskToSend = new Task(TaskCommand.MOVE_NOT_GRANTED, task, myReponsseQueue );
                this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(taskToSend));
            }

        } catch ( ClientNotFound err){
            this.logger.log("Error : " + err.toString());
            taskToSend = new Task(TaskCommand.CLIENT_NOT_FOUNDED, null , null);
             this.outChannel.basicPublish("",task.replyQueu, null, Communication.serialize(taskToSend));
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
     * @param cell the actual client cell
     */
    @Override
    public void checkForNeighbor(Cell cell, String queueClient) {
        Cell neighbour;
        Task contactServerNeighbor;

        try {
            neighbour = new Cell(cell.getX()+1, cell.getY());
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, neighbour , queueClient);
            if(mapService.whoManageCell(neighbour) != null)
                this.outChannel.basicPublish("",mapService.whoManageCell(neighbour), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            this.logger.log("Cell out of bounds");
        }

        try {
            neighbour = new Cell(cell.getX()-1, cell.getY());
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, neighbour , queueClient);
            if(mapService.whoManageCell(neighbour) != null)
                this.outChannel.basicPublish("",mapService.whoManageCell(neighbour), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            this.logger.log("Cell out of bounds");
        }

        try {
            neighbour = new Cell(cell.getX(), cell.getY()+1);
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, neighbour , queueClient);
            if(mapService.whoManageCell(neighbour) != null)
                this.outChannel.basicPublish("",mapService.whoManageCell(neighbour), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            this.logger.log("Cell out of bounds");
        }

        try {
            neighbour = new Cell(cell.getX(), cell.getY()-1);
            contactServerNeighbor = new Task(TaskCommand.NEIHGBOR, neighbour , queueClient);
            if(mapService.whoManageCell(neighbour) != null)
                this.outChannel.basicPublish("",mapService.whoManageCell(neighbour), null,  Communication.serialize(contactServerNeighbor));

        } catch (IOException | ZoneNotFound err) {
            this.logger.log("Cell out of bounds");
        }


    }

    @Override
    public void mayNeighbor(Task task) throws IOException {
        String client = mapService.isSomeOneWhere((Cell)task.cmd);
        if(client != null){
            Task t = new Task(TaskCommand.PING, null , task.replyQueu);
            this.outChannel.basicPublish("", client, null,  Communication.serialize(t));
        }
    }


    @Override
    public String print() {
        return null;
    }

}
