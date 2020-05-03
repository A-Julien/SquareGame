package Server;

import Configuration.RmqConfig;
import FX.Console;

import Server.RmqCom.ConnectionManger;
import Server.Sevices.MapService;
import Server.Sevices.TaskService;
import Utils.Communication;
import Utils.SimpleLogger;
import com.rabbitmq.client.*;

import java.io.IOException;
import Manager.Map.Zone;

import Exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import Task.*;


public class Server implements Runnable, RmqConfig {
    private static int ServerID;
    private String SERVER_NAME;
    private String RPC_INIT_QUEUE_NAME;
    private Connection connection;
    private Channel newClientChanel;

    private List<Zone> map;

    private String uniqueServeurQueue;


    private Integer serverZone;
    private Object monitor;


    private  String RMQ_HOST;

    private boolean initOk = false;

    private TaskService taskService;
    private MapService mapService;
    private SimpleLogger logger;



    public Server(String RPC_INIT_QUEUE_NAME, String RMQ_HOST) {
        this.SERVER_NAME = RMQ_BASIC_SERVER_NAME + ServerID++;
        this.RPC_INIT_QUEUE_NAME = RPC_INIT_QUEUE_NAME;
        this.RMQ_HOST = RMQ_HOST;
        this.logger = new SimpleLogger(this.SERVER_NAME);

    }

    @Override
    public void run() {
        try {
            this.logger.log("status : " + this.SERVER_NAME + " up");
            this.initCommunication();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Initialize all services and connection
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initCommunication() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.RMQ_HOST);
        try{
            this.connection = factory.newConnection();
        } catch ( Exception e ) {
            this.logger.log("Connection Failed");
            java.lang.System.exit(-1);
        }

        this.initClientCallbackInstruction();
        this.initWaitForNewClient();
        this.initConnectionInitMap();
        this.initConnectionRPC();
        this.waitForAllServersReady();
    }

    /**
     * Init Server services
     *
     * @throws IOException
     */
    private void initServices() throws IOException {
        Channel outChannel = this.connection.createChannel();
        try {
            this.mapService = new MapService(this.map, this.serverZone);
        } catch (ZoneNotFound zoneNotFound) {
           this.logger.log("Error while MapService start : " + zoneNotFound.toString());
           System.exit(-1);
        }
        this.taskService = new TaskService(outChannel, this.mapService, uniqueServeurQueue, this.logger);

    }

    /**
     * Initialize connection to the manager
     */
    private void initConnectionRPC(){
        try {
            this.logger.log("Requesting Initialisation from manager");
            this.logger.log("Getting data from manager");
            this.serverZone = ConnectionManger.init(this.connection, this.RPC_INIT_QUEUE_NAME, this.uniqueServeurQueue);
            this.logger.log("Connection fully establish");
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait instruction from client
     *
     * @throws IOException
     */
    private void initClientCallbackInstruction() throws IOException{
        Channel incomingInstruction = connection.createChannel();
        this.uniqueServeurQueue =  incomingInstruction.queueDeclare("", true, false, false, null).getQueue();
        incomingInstruction.basicQos(1);
        this.logger.log("Server status : " + this.SERVER_NAME + " queue declare" + uniqueServeurQueue);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            try {
                Task task = (Task) Communication.deserialize(delivery.getBody());
                this.logger.log(" [x] New Task there'" + task.toString() + "'");
                taskService.compute(task);
            } catch (ClassNotFoundException | UnknownCmd e) {
                e.printStackTrace();
                this.logger.log("Problem during task");
            }
        };
        incomingInstruction.basicConsume(this.uniqueServeurQueue, true, deliverCallback, consumerTag -> { });
    }

    /**
     * Wait for new ClientFX
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initWaitForNewClient() throws IOException, TimeoutException{
        this.newClientChanel = connection.createChannel();
        this.newClientChanel.queueDeclare(POOL_CLIENT_QUEUE, false, false, false, null);
        this.newClientChanel.queuePurge(POOL_CLIENT_QUEUE);

        this.newClientChanel.basicQos(1);


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();


            Task task = null;
            try {
                task = (Task) Communication.deserialize(delivery.getBody());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            this.logger.log("New client there ");
            this.logger.log("try to find place for client");


            try {
                this.newClientChanel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);   //si place dans la map
                Task taskSend = new Task(TaskCommand.INIT,  mapService.initPositionClient(task.replyQueu), uniqueServeurQueue);
                this.newClientChanel.basicPublish("", task.replyQueu, replyProps, Communication.serialize(taskSend));

            } catch (CellNotFound e){
                this.newClientChanel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);   //si pas place
            }
        };

        newClientChanel.basicConsume(POOL_CLIENT_QUEUE, false, deliverCallback, (consumerTag -> { }));
    }



    /**
     * Wait for download game map
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initConnectionInitMap() throws IOException {
        Channel initMap = connection.createChannel();
        initMap.exchangeDeclare(INITMAP_EXCHANGE, "fanout");
        String queueName = initMap.queueDeclare().getQueue();
        initMap.queueBind(queueName, INITMAP_EXCHANGE, "");

        monitor = new Object();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            this.logger.log("Received map from manager");
            try {
                this.map = (ArrayList<Zone>) Communication.deserialize(delivery.getBody());
                this.initServices();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized (monitor) {
                monitor.notify();
            }
            this.initOk = true;
        };
        initMap.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    /**
     * Synchronise Server
     */
    private void waitForAllServersReady(){
        if (!initOk) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
