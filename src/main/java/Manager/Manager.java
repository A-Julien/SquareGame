package Manager;

import Configuration.RmqConfig;
import Server.Server;
import Utils.Communication;
import com.rabbitmq.client.*;
import Exception.MapNotSetException;
import Exception.ServerNotSetException;
import Manager.Map.Zone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class Manager {
    private int zoneCounter = 0;
    private List<Zone> zoneList = null;
    private String rmqServerIp;
    private MetaDataServer metaDataServer;
    private int nbThreads;
    private ThreadPoolExecutor executor;
    private Connection connection;
    private Integer ServerCount = 0;
    private final static Object monitor = new Object();

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
    connection = factory.newConnection();

    try (Channel channel = connection.createChannel()) {

        channel.queueDeclare(RmqConfig.RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RmqConfig.RPC_QUEUE_NAME);

        channel.basicQos(1);

        System.out.println("[MANAGER] Manager connected to RmqServer");

        //monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("[MANAGER] New Server found  " + message);
                this.setServerZone(message);

                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, Communication.serialize(this.giveZone()));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                ServerCount++;
                System.out.println("[MANAGER] " + ServerCount + " server found of " + this.metaDataServer.getNbServer());
                if(ServerCount == this.metaDataServer.getNbServer()) {
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
        };

        System.out.println("[MANAGER] Awaiting " + this.metaDataServer.getNbServer() + " RPC requests from Server");
        channel.basicConsume(RmqConfig.RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));

        this.createServer();

        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }

        //Thread.sleep(2000);

        Channel channelBroadcastServer = connection.createChannel();
        channelBroadcastServer.exchangeDeclare("INITMAP", "fanout");

        System.out.println("[MANAGER] All server connected, sending map");

        channelBroadcastServer.basicPublish(RmqConfig.INITMAP_EXCHANGE, "", null, Communication.serialize(this.zoneList));

        if(this.metaDataServer.getNbLocalSever() != 0) this.executor.shutdown();
    }
}


    private void setServerZone(String serverQueueName){
        this.zoneList.get(this.zoneCounter).setServerQueueName(serverQueueName);
    }

    private Integer giveZone(){
        this.zoneCounter++;
        return this.zoneList.get(this.zoneCounter - 1).getId();
    }

    /**
     * Create Server threads
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void createServer() throws IOException, TimeoutException {
        if(this.metaDataServer.getNbLocalSever() == 0) return;
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
        for(Zone zone : this.zoneList) {
            this.metaDataServer.addServer(zone.getIp(),zone.getPort());
        }
        System.out.println("cc");
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