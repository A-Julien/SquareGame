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
import java.util.concurrent.TimeoutException;

public class Server extends Console implements Runnable  {
    private String SERVER_NAME;
    private String RPC_INI_QUEUE_NAME;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel information;
    private Channel broadcast;
//    private Channel newClient;
    private Channel work;

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



    }

    @Override
    public void run() {
        try {
            System.out.println("Server status : " + this.SERVER_NAME + " up");
            this.initConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void initQueuCommunication() throws IOException{
        work = connection.createChannel();
        uniqueServeurQueue = work.queueDeclare().getQueue();
        System.out.println("Server status : " + this.SERVER_NAME + " queue declare" + uniqueServeurQueue);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            try {
                TaskServer t = (TaskServer) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + t.toString() + "'");
                System.out.println(t.handle(work,broadcast));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }
            work.basicAck(delivery.getEnvelope().getDeliveryTag(), false);





        };
        work.basicConsume(uniqueServeurQueue, false, deliverCallback, consumerTag -> { });


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

            String response = "";


            System.out.println("New client there");
            information.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, uniqueServeurQueue.getBytes("UTF-8"));
            information.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            // RabbitMq consumer worker thread notifies the RPC server owner thread
            synchronized (soloClient) {
                soloClient.notify();
            }


        };

        information.basicConsume(POOL_CLIENT_QUEUE, false, deliverCallback, (consumerTag -> { }));
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
        this.initConnectionFANOUT();
        this.initConnectionRPC();
        this.waitForAllServersReady();

    }

    private void initConnectionFANOUT() throws IOException, TimeoutException {

        this.broadcast = connection.createChannel();
        broadcast.exchangeDeclare("BROADCAST", "fanout");
        String queueName = broadcast.queueDeclare().getQueue();
        broadcast.queueBind(queueName, "BROADCAST", "");

        monitor = new Object();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            synchronized (monitor) {
                monitor.notify();
            }
            initOk = true;
        };
        broadcast.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private void initConnectionRPC() throws IOException, TimeoutException {
            try (RPC_INIT rpcInit = new RPC_INIT(this.connection, this.RPC_INI_QUEUE_NAME, this.uniqueServeurQueue)) {
                System.out.println(" [x] Requesting Initialisation from manager");
                this.informationsServeur = rpcInit.call();
                System.out.println(" [.] Got IT");
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


}
