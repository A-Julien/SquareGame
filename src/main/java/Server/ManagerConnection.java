package Server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class ManagerConnection implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    InformationsServeur informationsServeur;
    String requestQueueName;
    String uniqueQueueServer;

    public ManagerConnection(Connection connection, String requestQueueName, String uniqueQueueServer) throws IOException {
        this.connection = connection;
        channel = connection.createChannel();
        this.requestQueueName = requestQueueName;
        this.uniqueQueueServer = uniqueQueueServer;
    }


    public InformationsServeur call() throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props,  uniqueQueueServer.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        informationsServeur = new InformationsServeur(result);
        channel.basicCancel(ctag);
        return informationsServeur;
    }

    public void close() throws IOException {
       // connection.close();
    }
}
