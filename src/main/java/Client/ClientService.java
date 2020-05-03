package Client;

import Configuration.RmqConfig;
import FX.Client.ClientFX;
import Manager.Map.Cell;
import Utils.Direction;
import Task.Task;
import Task.TaskCommand;
import Utils.Communication;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static Configuration.RmqConfig.RMQ_SERVER_IP;

public class ClientService  implements ClientReaction{
    private Channel sendChanel;
    private Channel receivedChanel;

    private static String queuServer;
    private String clientQueue;

    private static final Object monitor = new Object();
    private ClientFX clientFX;

    public ClientService(ClientFX clientFX) throws IOException, TimeoutException {
        this.clientFX = clientFX;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RMQ_SERVER_IP);
        Connection connection = factory.newConnection();

        this.sendChanel = connection.createChannel();
        this.receivedChanel = connection.createChannel();
        this.clientQueue = this.receivedChanel.queueDeclare().getQueue();

        this.initClientCommunication();

        Task TaskToSend = new Task(TaskCommand.INIT, null, clientQueue);
        this.sendChanel.basicPublish("", RmqConfig.POOL_CLIENT_QUEUE, null, Communication.serialize(TaskToSend));
    }

    /**
     * Init Communication between server
     * Define client reaction
     * @throws IOException serialization error
     */
    private void initClientCommunication() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                Task task = (Task) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + task.toString() + "'");
                synchronized (monitor){
                    monitor.notify();
                }
                switch (task.cmdType){
                    case INIT:
                        queuServer = task.replyQueu;
                        this.clientFX.setPosCircle(((Cell) task.cmd).getX(), ((Cell) task.cmd).getY());
                        this.requestServerColor();
                        System.out.println("On m'as assigné la position" + ((Cell) task.cmd));
                        break;
                    case MOVE_GRANTED:
                        this.clientFX.moveCircle((Cell) task.cmd);
                        break;
                    case PING:
                        System.out.println("BONJOUR WESH");
                        this.reactToPing(task);
                        break;
                    case PONG:
                        System.out.println("BONJOUR MA GUEULE");
                        this.reactToPong();
                        break;
                    case GET_COLOR:
                        this.changeColor(task);
                        break;
                    case MOVE_GRANTED_FORWARDED:
                        queuServer = task.replyQueu;
                        this.clientFX.moveCircle((Cell) task.cmd);
                        this.requestServerColor();
                        break;
                    default:

                }

            } catch (ClassNotFoundException  e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }

        };
        this.receivedChanel.basicConsume(clientQueue, true, deliverCallback, consumerTag -> { });
    }

    /**
     * Request color to server
     * @throws IOException
     */
    private void requestServerColor() throws IOException {
        Task TaskToSend = new Task(TaskCommand.GET_COLOR,null, clientQueue);
        this.sendChanel.basicPublish("",queuServer, null, Communication.serialize(TaskToSend));
    }

    /**
     * Call server for action.
     * This method are trigger by the FX
     *
     * @param movement the action
     * @throws IOException
     */
    public void handleMovement(Direction movement) throws IOException {
        System.out.println("Déplacement souhaité : " + movement);
        Task TaskToSend = new Task(TaskCommand.MOVE,movement, clientQueue);
        this.sendChanel.basicPublish("",queuServer, null, Communication.serialize(TaskToSend));
        synchronized(monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * change FX color when client move to a other server
     * @param task
     */
    @Override
    public void changeColor(Task task) {
        this.clientFX.setColorGrid(
                Color.color(((Manager.Map.Color)task.cmd).red,
                        ((Manager.Map.Color )task.cmd).green,
                        ((Manager.Map.Color )task.cmd).blue
                )
        );
    }

    /**
     * react to a client pong
     */
    @Override
    public void reactToPong() {
        this.clientFX.showAlert1();
    }

    @Override
    public void reactToPing(Task task) throws IOException {
        this.clientFX.showAlert1();
        Task t = new Task(TaskCommand.PONG, null, null);
        sendChanel.basicPublish("", task.replyQueu, null, Communication.serialize(t));
    }

    @Override
    public void handleMouvement(Direction mouvement) {

    }
}