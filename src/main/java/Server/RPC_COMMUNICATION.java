package Server;
import Class.InformationsServeur;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPC_COMMUNICATION implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    InformationsServeur informationsServeur;
    String requestQueueName;

    public RPC_COMMUNICATION(Connection connection, String requestQueueName) throws IOException, TimeoutException {
        this.connection = connection;
        channel = connection.createChannel();
        this.requestQueueName = requestQueueName;
    }


    public String call() throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, null);

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();

        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        // connection.close();
    }
}