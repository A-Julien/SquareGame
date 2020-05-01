package Manager;
import Configuration.RmqConfig;
import com.rabbitmq.client.*;

public class ManagerDev implements RmqConfig {
    int i = 0;
    private static final String RPC_QUEUE_NAME = "rpc_queue_init";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.102");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                   response += "Ca fonctionne";
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }

                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            int i = 0;
            while (i < 1 ) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                    // Do only two task
                    i++;
                }
            }

            Channel channel2 = connection.createChannel();
                channel2.exchangeDeclare("BROADCAST", "fanout");

                String message = argv.length < 1 ? "All server are initialized, let's go" :
                        String.join(" ", argv);

                channel2.basicPublish("BROADCAST", "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
        }

    }
}