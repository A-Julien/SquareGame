package Server;
import FX.Console;
import Class.InformationsServeur;
import Class.Task;
import Utils.Communication;
import com.rabbitmq.client.*;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.cassandra.locator.Ec2Snitch;

import java.io.IOException;
import Class.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Server extends Console implements Runnable  {
    private String SERVER_NAME;
    private String RPC_INI_QUEUE_NAME;
    private String BROADCAST_QUEUE;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel information;
    private Channel broadcast;
    private Channel initMap;
    private Channel talkBroadcast;
//    private Channel newClient;
    private Channel work;
    private List<Zone> map;

    // Propre à chaque serveur
    private String uniqueServeurQueue;


    private InformationsServeur informationsServeur;
    private Object monitor;

    private Object soloClient;

    final String TASK_QUEUE_NAME = "task_queue";
    private final String POOL_CLIENT_QUEUE = "new_client";
    private  String RMQ_HOST;
    private Console console;

    boolean initOk = false;



    public Server(String RPC_INI_QUEUE_NAME, String RMQ_HOST, String SERVER_NAME) throws IOException, TimeoutException {
        super();
        this.SERVER_NAME = SERVER_NAME;
        this.RPC_INI_QUEUE_NAME = RPC_INI_QUEUE_NAME;
        this.RMQ_HOST = RMQ_HOST;
       // connection.close();
    }

    @Override
    public void run() {
        try {
            this.log("status : " + this.SERVER_NAME + " up");
            this.initConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void initQueuCommunication() throws IOException{
        work = connection.createChannel();
        uniqueServeurQueue =  work.queueDeclare("", true, false, false, null).getQueue();

        this.log("Server status : " + this.SERVER_NAME + " queue declare" + uniqueServeurQueue);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {


            try {
                TaskServer t = (TaskServer) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + t.toString() + "'");
                System.out.println(t.handle(work,talkBroadcast));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }
          //  work.basicAck(delivery.getEnvelope().getDeliveryTag(), false);





        };
        work.basicConsume(uniqueServeurQueue, true, deliverCallback, consumerTag -> { });


    }
    private void initCommunicationClient() throws IOException, TimeoutException{
        information = connection.createChannel();
        information.queueDeclare(POOL_CLIENT_QUEUE, false, false, false, null);
        information.queuePurge(POOL_CLIENT_QUEUE);

        information.basicQos(1);

        soloClient = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();




            System.out.println("New client there");
            information.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, uniqueServeurQueue.getBytes("UTF-8"));
            // RabbitMq consumer worker thread notifies the RPC server owner thread
            synchronized (soloClient) {
              //  soloClient.notify();
            }


        };

        information.basicConsume(POOL_CLIENT_QUEUE, true, deliverCallback, (consumerTag -> { }));
    }

    private void initConnection() throws IOException, TimeoutException {
        this.factory = new ConnectionFactory();
        this.factory.setHost(this.RMQ_HOST);
        try{
            this.connection = factory.newConnection();
        } catch ( Exception e ) {
            System.out.println("Connection Failed");
            java.lang.System.exit(-1);

        }
        this.initQueuCommunication();
        this.initCommunicationClient();
        this.initConnectionInitMap();
        this.initConnectionFANOUT();
        this.initConnectionRPC();
        this.waitForAllServersReady();

    }

    private void initConnectionFANOUT() throws IOException, TimeoutException {
        this.talkBroadcast = connection.createChannel();
        this.talkBroadcast.exchangeDeclare("BROADCAST", "fanout");

        this.broadcast = connection.createChannel();
        this.broadcast.exchangeDeclare("BROADCAST", "fanout");
        this.BROADCAST_QUEUE = broadcast.queueDeclare().getQueue();
        this.broadcast.queueBind(BROADCAST_QUEUE, "BROADCAST", "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                TaskServer t = (TaskServer) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] BOADCATS MESSAGE : '" + t.toString() + "'");
                System.out.println(t.handle(work,talkBroadcast));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during BROADCAST");
            }


        };


        this.broadcast.basicConsume(BROADCAST_QUEUE, true, deliverCallback, consumerTag -> { });

    }

    private void initConnectionInitMap() throws IOException, TimeoutException {

        this.initMap = connection.createChannel();
        this.initMap.exchangeDeclare("INITMAP", "fanout");
        String queueName = initMap.queueDeclare().getQueue();
        this.initMap.queueBind(queueName, "INITMAP", "");

        monitor = new Object();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            this.log("Received map from manager");
            try {
                this.map = (ArrayList<Zone>) Communication.deserialize(delivery.getBody());
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

    private void initConnectionRPC(){
            try (RPC_INIT rpcInit = new RPC_INIT(this.connection, this.RPC_INI_QUEUE_NAME, this.uniqueServeurQueue)) {
                this.log("Requesting Initialisation from manager");
                this.log("Getting data from manager");
                this.informationsServeur = rpcInit.call();
                this.log("Connection fully establish");
            } catch (IOException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
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
