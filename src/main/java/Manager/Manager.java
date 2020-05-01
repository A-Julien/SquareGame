package Manager;

import Configuration.RmqConfig;
import Server.Server;
import Utils.Communication;
import com.rabbitmq.client.*;
import Exception.MapNotSetException;
import Exception.ServerNotSetException;
import Class.Zone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class Manager {
    private int i = 0;
    private List<Zone> zoneList = null;
    private String rmqServerIp;
    private MetaDataServer metaDataServer;
    private int nbThreads;
    private ThreadPoolExecutor executor;

    public Manager(String rmqServerIp, int nbThreads) {
        this.rmqServerIp = rmqServerIp;
        this.metaDataServer = new MetaDataServer();
        this.nbThreads = nbThreads;
    }

    public Manager(int nbThreads){
        this.rmqServerIp = RmqConfig.RMQ_SERVER_IP;
        this.metaDataServer = new MetaDataServer();
        this.nbThreads = nbThreads;
    }

    /**
     * Allow manager to run
     * @throws MapNotSetException just security, can not start if map not set
     * @throws ServerNotSetException just security, can not start if Server not set
     */
    public void run() throws MapNotSetException, ServerNotSetException, IOException, TimeoutException {
        this.digestServer();
        this.requireMap();
        this.requireServerExtracted();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.rmqServerIp);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(RmqConfig.RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RmqConfig.RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println("[MANAGER] Manager connected to RmqServer");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println("[MANAGER] New Server found  " + message);
                } catch (RuntimeException e) {
                    System.out.println("[MANAGER] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }

                }
            };
            System.out.println("[MANAGER] Awaiting RPC requests from Server");
            channel.basicConsume(RmqConfig.RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));

            this.createServer();

            // Wait and be prepared to consume the message from RPC client.
            int i = 0;
            while (i < this.metaDataServer.getNbLocalSever()) {
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

            Channel channelBroadcastServer = connection.createChannel();
            channelBroadcastServer.exchangeDeclare("BROADCAST", "fanout");

            System.out.println("[MANAGER] All server connected, sending map");

            channelBroadcastServer.basicPublish("BROADCAST", "", null, Communication.serialize(this.zoneList));

            this.executor.shutdown();
        }
    }

    /**
     * Create Server threads
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void createServer() throws IOException, TimeoutException {
        System.out.println("[MANAGER] Launching local server");
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.nbThreads);
        for (int i = 1; i <= this.metaDataServer.getNbLocalSever(); i++){
            this.executor.execute(
                    new Server(
                            RmqConfig.RPC_QUEUE_NAME,
                            RmqConfig.RMQ_SERVER_IP,
                            "rmq-server-"+ i));
        }
    }

    /**
     * Extract metadata of server from the map
     *
     * @throws MapNotSetException just require map
     */
    private void digestServer() throws MapNotSetException {
        this.requireMap();
        for(Zone zone : this.zoneList)
            this.metaDataServer.addServer(zone.getIp(),zone.getPort());
    }

    /**
     * Set the map
     * @param zones the game map
     */
    public void setMap(ArrayList<Zone> zones){
        this.zoneList = zones;
    }

    private void requireMap() throws MapNotSetException {
        if (this.zoneList == null) throw new MapNotSetException("Map not set");
    }

    private void requireServerExtracted() throws ServerNotSetException {
        if(this.metaDataServer.getServerListInfo() == null) throw new ServerNotSetException("No configuration server found");
    }
}