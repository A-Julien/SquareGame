package Manager;

import Configuration.RmqConfig;
import Server.Server;
import Utils.Communication;
import Utils.SimpleLogger;
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

    private SimpleLogger logger;

    public Manager(String rmqServerIp, int nbThreads) {
        this.rmqServerIp = rmqServerIp;
        this.metaDataServer = new MetaDataServer();
        this.nbThreads = nbThreads;
    }

    public Manager(int nbThreads) {
        this.rmqServerIp = RmqConfig.RMQ_SERVER_IP;
        this.metaDataServer = new MetaDataServer();
        this.nbThreads = nbThreads;
        this.logger = new SimpleLogger("MANAGER");
    }

    /**
     * Allow manager to run
     *
     * @throws MapNotSetException    just security, can not start if map not set
     * @throws ServerNotSetException just security, can not start if Server not set
     */
    public void run() throws MapNotSetException, ServerNotSetException, IOException, TimeoutException {
        this.digestServer();
        this.requireMap();
        this.requireServerExtracted();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.rmqServerIp);
        connection = factory.newConnection();

        this.initServerSubChanel();
        this.createServer();
        this.synchro();

        Channel channelBroadcastServer = connection.createChannel();
        channelBroadcastServer.exchangeDeclare("INITMAP", "fanout");
        this.logger.log("All server connected, sending map");
        channelBroadcastServer.basicPublish(RmqConfig.INITMAP_EXCHANGE, "", null, Communication.serialize(this.zoneList));
        if (this.metaDataServer.getNbLocalSever() != 0) this.executor.shutdown();
    }

    /**
     * wait all server ready
     */
    private void synchro(){
        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     *
     * Init chanel for servers.
     * Server can connect to the manager by this method.
     * When server connected, the manager get the server queue and update the map
     *
     * @throws IOException
     */
    private void initServerSubChanel() throws IOException {
            Channel channel = connection.createChannel();

            channel.queueDeclare(RmqConfig.RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RmqConfig.RPC_QUEUE_NAME);

            channel.basicQos(1);

            this.logger.log("Manager connected to RmqServer");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();
                String message = new String(delivery.getBody(), "UTF-8");

                this.logger.log("New Server found  " + message);

                this.setServerZone(message);
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, Communication.serialize(this.giveZone()));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                ServerCount++;
                this.logger.log(ServerCount + " server found of " + this.metaDataServer.getNbServer());

                if (ServerCount == this.metaDataServer.getNbServer()) {
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            this.logger.log("Awaiting " + this.metaDataServer.getNbServer() + " RPC requests from Server");
            channel.basicConsume(RmqConfig.RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
    }


    /**
     * Assign a server to a zone
     *
     * @param serverQueueName the server queue name
     */
    private void setServerZone(String serverQueueName) {
        this.zoneList.get(this.zoneCounter).setServerQueueName(serverQueueName);
    }

    /**
     * Give a zone for a new server
     *
     * @return zone id
     */
    private Integer giveZone() {
        this.zoneCounter++;
        return this.zoneList.get(this.zoneCounter - 1).getId();
    }

    /**
     * Create Server threads
     */
    private void createServer(){
        if (this.metaDataServer.getNbLocalSever() == 0) return;
        this.logger.log("Launching local server");
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.nbThreads);
        for (int i = 1; i <= this.metaDataServer.getNbLocalSever(); i++) {
            this.executor.execute(
                    new Server(
                            RmqConfig.RPC_QUEUE_NAME,
                            RmqConfig.RMQ_SERVER_IP,
                            null));
        }
    }

    /**
     * Extract metadata of server from the map
     *
     * @throws MapNotSetException just require map
     */
    private void digestServer() throws MapNotSetException {
        this.requireMap();
        for (Zone zone : this.zoneList) {
            this.metaDataServer.addServer(zone.getIp(), zone.getPort());
        }
    }

    /**
     * Set the map
     *
     * @param zones the game map
     */
    public void setMap(ArrayList<Zone> zones) {
        this.zoneList = zones;
    }

    private void requireMap() throws MapNotSetException {
        if (this.zoneList == null) throw new MapNotSetException("Map not set");
    }

    /**
     * Ensure that the map have been loaded
     *
     * @throws ServerNotSetException if map are not set
     */
    private void requireServerExtracted() throws ServerNotSetException {
        if (this.metaDataServer.getServerListInfo() == null)
            throw new ServerNotSetException("No configuration server found");
    }
}