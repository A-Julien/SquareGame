package Server.RmqCom;

import Utils.Communication;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class ConnectionManger implements AutoCloseable {

    public static Integer init(Connection connection, String requestQueueName, String uniqueQueueServer) throws IOException, InterruptedException, ClassNotFoundException {
        Channel channel = connection.createChannel();
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props,  uniqueQueueServer.getBytes("UTF-8"));

        final BlockingQueue<byte[]> response = new ArrayBlockingQueue<>(1);

        channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) response.offer(delivery.getBody());

            },
                consumerTag -> { });

        return (Integer) Communication.deserialize(response.take());
    }

    @Override
    public void close() throws Exception { }
}
