package Client;
import Configuration.RmqConfig;
import Server.TaskServer;
import Server.RPC_COMMUNICATION;
import com.rabbitmq.client.*;
import Utils.Communication;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SimulationClient implements RmqConfig {


    private static final String TASK_QUEUE_NAME = "task_queue";
    private static String queueCom;
    private static String myQueue;


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RMQ_SERVER_IP);
        Connection connection = factory.newConnection();
        try (RPC_COMMUNICATION rpcC = new RPC_COMMUNICATION(connection, "new_client")) {
            System.out.println(" [x] Request from a server");
            queueCom = rpcC.call();
            System.out.println(" [.] Queue = " + queueCom);
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sending message");
        Channel channel = connection.createChannel();
        Channel recevoirInformation = connection.createChannel();

        myQueue = channel.queueDeclare().getQueue();
        System.out.println("Ma queue " + myQueue);
        TaskServer task = new TaskServer("MOVE 10 12", myQueue);

        channel.basicPublish("", queueCom, null, Communication.serialize(task));

        Thread.sleep(5000);
        System.out.println("Deuxieme envoi");
        channel.basicPublish("", queueCom, null, Communication.serialize(task));

        //message = "Au revoir!";
        //channel.basicPublish("", queueCom, null, message.getBytes(StandardCharsets.UTF_8));

    }

}