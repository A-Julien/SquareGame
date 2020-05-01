package Server;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SimulationClient {

    private static final String TASK_QUEUE_NAME = "task_queue";
    private static String queueCom;


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        try (RPC_COMMUNICATION rpcC = new RPC_COMMUNICATION(connection, "new_client")) {
            System.out.println(" [x] Request from a server");
            queueCom = rpcC.call();
            System.out.println(" [.] Queue = " + queueCom);
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }

        Channel channel = connection.createChannel();

        String message = "Coucou!";
        for(int i = 0; i < 10; i++){
            channel.basicPublish("", queueCom, null, message.getBytes(StandardCharsets.UTF_8));
        }
        message = "Au revoir!";
        channel.basicPublish("", queueCom, null, message.getBytes(StandardCharsets.UTF_8));

    }

}