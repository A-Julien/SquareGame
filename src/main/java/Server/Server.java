package Server;
import Configuration.RmqConfig;
import FX.Console;

import Utils.Communication;
import com.rabbitmq.client.*;

import java.io.IOException;
import Manager.Map.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;



public class Server extends Console implements Runnable, RmqConfig {
    private String SERVER_NAME;
    private String BROADCAST_QUEUE;
    private String RPC_INIT_QUEUE_NAME;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel newClientChanel;
    private Channel recievedBroadcastChanel;
    private Channel initMap;
    private Channel sendBroadcastChanel;
    private Channel outChannel;

//    private Channel newClient;
    private Channel incomingInstruction;
    private List<Zone> map;

    // Propre à chaque serveur
    private String uniqueServeurQueue;


    private String serverZone;
    private Object monitor;

    private Object soloClient;

    private  String RMQ_HOST;
    private Console console;

    boolean initOk = false;

    private ComputeTaskServer computeTaskServer;



    public Server(String RPC_INIT_QUEUE_NAME, String RMQ_HOST, String SERVER_NAME) throws IOException, TimeoutException {
        super();
        this.SERVER_NAME = SERVER_NAME;
        this.RPC_INIT_QUEUE_NAME = RPC_INIT_QUEUE_NAME;
        this.RMQ_HOST = RMQ_HOST;
    }

    @Override
    public void run() {
        try {
            this.log("status : " + this.SERVER_NAME + " up");
            this.initServices();
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
    private void initServices() throws IOException, TimeoutException {
        this.factory = new ConnectionFactory();
        this.factory.setHost(this.RMQ_HOST);
        try{
            this.connection = factory.newConnection();
        } catch ( Exception e ) {
            System.out.println("Connection Failed");
            java.lang.System.exit(-1);
        }

        this.initClientCallbackInstruction();
        this.initWaitForNewClient();
        this.initConnectionInitMap();
        this.initBroadcastServerMessaging();
        this.initConnectionRPC();
        this.waitForAllServersReady();


    }

    private void initComputeTask() throws IOException {
        this.outChannel = this.connection.createChannel();
        this.computeTaskServer = new ComputeTaskServer(this.outChannel, this.sendBroadcastChanel, this.map, uniqueServeurQueue);
    }

    /**
     * Initialize connection to the manager
     */
    private void initConnectionRPC(){
        try (ManagerConnection rpcInit = new ManagerConnection(this.connection, this.RPC_INIT_QUEUE_NAME, this.uniqueServeurQueue)) {
            this.log("Requesting Initialisation from manager");
            this.log("Getting data from manager");
            this.serverZone = rpcInit.call();
            this.log("Connection fully establish");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait instruction from client
     *
     * @throws IOException
     */
    private void initClientCallbackInstruction() throws IOException{
        this.incomingInstruction = connection.createChannel();
        this.uniqueServeurQueue =  incomingInstruction.queueDeclare("", true, false, false, null).getQueue();

        this.log("Server status : " + this.SERVER_NAME + " queue declare" + uniqueServeurQueue);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {


            try {
                ComputeTaskServer t = (ComputeTaskServer) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + t.toString() + "'");
                //System.out.println(t.compute(work,talkBroadcast));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }
          //  work.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        this.incomingInstruction.basicConsume(this.uniqueServeurQueue, true, deliverCallback, consumerTag -> { });


    }

    /**
     * Wait for new Client
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initWaitForNewClient() throws IOException, TimeoutException{
        this.newClientChanel = connection.createChannel();
        this.newClientChanel.queueDeclare(POOL_CLIENT_QUEUE, false, false, false, null);
        this.newClientChanel.queuePurge(POOL_CLIENT_QUEUE);

        this.newClientChanel.basicQos(1);

        //this.soloClient = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            if(false) {
                this.newClientChanel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);   //si place dans la map
            } else {
                this.newClientChanel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);   //si pas place
            }

            this.log("try to find place for client");


            //TODO verifier si de la place dispo dans ses case
            System.out.println("New client there");
            this.newClientChanel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, uniqueServeurQueue.getBytes("UTF-8"));
            // RabbitMq consumer worker thread notifies the RPC server owner thread
            //synchronized (soloClient) {
              //  soloClient.notify();
            //}


        };

        newClientChanel.basicConsume(POOL_CLIENT_QUEUE, false, deliverCallback, (consumerTag -> { }));
    }


    /**
     * Initialize Broadcast connection with all Server
     *
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initBroadcastServerMessaging() throws IOException, TimeoutException {
        this.sendBroadcastChanel = connection.createChannel();
        this.sendBroadcastChanel.exchangeDeclare(BROADCAST_EXCHANGE, "fanout");

        this.recievedBroadcastChanel = connection.createChannel();
        this.recievedBroadcastChanel.exchangeDeclare(BROADCAST_EXCHANGE, "fanout");
        this.BROADCAST_QUEUE = recievedBroadcastChanel.queueDeclare().getQueue();
        this.recievedBroadcastChanel.queueBind(BROADCAST_QUEUE, BROADCAST_EXCHANGE, "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                ComputeTaskServer t = (ComputeTaskServer) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + t.toString() + "'");
                //System.out.println(t.compute(work,broadcast));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during BROADCAST");
            }
        };
        this.recievedBroadcastChanel.basicConsume(BROADCAST_QUEUE, true, deliverCallback, consumerTag -> { });

    }

    /**
     * Wait for download game map
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private void initConnectionInitMap() throws IOException {

        this.initMap = connection.createChannel();
        this.initMap.exchangeDeclare(INITMAP_EXCHANGE, "fanout");
        String queueName = initMap.queueDeclare().getQueue();
        this.initMap.queueBind(queueName, INITMAP_EXCHANGE, "");

        monitor = new Object();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            this.log("Received map from manager");
            try {
                this.map = (ArrayList<Zone>) Communication.deserialize(delivery.getBody());
                this.initComputeTask();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized (monitor) {
                monitor.notify();
            }
            this.initOk = true;
        };
        this.initMap.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }


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

    private void log(String message){
        System.out.println("[" + this.SERVER_NAME + "] " + message);
    }

}
