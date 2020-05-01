package FX;

import FX.Map.PositionGrille;
import Configuration.RmqConfig;

import Utils.Communication;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import Client.RPC_COMMUNICATION;

public class Client extends Scene implements RmqConfig {
    BorderPane borderPane;
    Grid grid;
    Channel envoyerInformation;

    PositionGrille pos;
    int i;

    private static final String TASK_QUEUE_NAME = "task_queue";
    private static String queueCom;
    private static String myQueue;

    public Client(double largeur, double hauteur) throws IOException, TimeoutException {
        super(new BorderPane(),  largeur,  hauteur);
        borderPane = (BorderPane) this.getRoot();

        grid = new Grid(10,10,hauteur*0.9,largeur*0.9, false);

        this.pos = new PositionGrille(0,0);
        grid.affCircle();
        borderPane.setCenter(grid);



        EventHandler<KeyEvent> clavier = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                PositionGrille p = new PositionGrille(0,0);
                boolean dep = false;
                if(key.getCode()== KeyCode.Q || key.getCode()== KeyCode.LEFT) {
                    System.out.println("Déplacement gauche");
                    p.setX(-1);
                } else if(key.getCode()== KeyCode.D || key.getCode()== KeyCode.RIGHT) {
                    System.out.println("Déplacement droite");
                    p.setX(1);
                } else if(key.getCode()== KeyCode.Z || key.getCode()== KeyCode.UP) {
                    System.out.println("Déplacement Haut");
                    p.setY(-1);
                } else if(key.getCode()== KeyCode.S || key.getCode()== KeyCode.DOWN) {
                    System.out.println("Déplacement Bas");
                    p.setY(1);
                }

                try {
                    handleMouvement(p);
                } catch (IOException e) {
                    System.out.println("Impossible de communiquer avec le serveur");
                    e.printStackTrace();
                }

            }
        };
        this.addEventFilter(KeyEvent.KEY_PRESSED, clavier);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RMQ_SERVER_IP);
        Connection connection = factory.newConnection();
        try (RPC_COMMUNICATION rpcC = new RPC_COMMUNICATION(connection, "new_client")) {
            System.out.println(" [x] Request from a server");
            queueCom = rpcC.call();
            System.out.println(" [.] Queue = " + queueCom);
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sending message");
        this.envoyerInformation = connection.createChannel();
        Channel recevoirInformation = connection.createChannel();

        myQueue = recevoirInformation.queueDeclare().getQueue();
        System.out.println("Ma queue " + myQueue);


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            /*try {
                //TaskClient t = (TaskClient) Communication.deserialize(delivery.getBody());
                //System.out.println(" [x] New Task there'" + t.toString() + "'");
                //System.out.println(t.handle(envoyerInformation, myQueue));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }*/
           // recevoirInformation.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        };
        recevoirInformation.basicConsume(myQueue, true, deliverCallback, consumerTag -> { });

    }

    public void handleMouvement(PositionGrille mouvement) throws IOException {
        System.out.println("Déplacement souhaité : " + mouvement);
       // AskServer mouvement
        //TaskServer task = new TaskServer("MOVE " + mouvement.getX() +" " +mouvement.getY(), myQueue);
        //envoyerInformation.basicPublish("", queueCom, null, Communication.serialize(task));

        PositionGrille newP = new PositionGrille(pos.getX() + mouvement.getX(), pos.getY() + mouvement.getY());
        System.out.println(newP);
        mouvement(newP);
        //setColorGrid(Color.BLUE);
    }

    public void mouvement(PositionGrille p){
        pos = p;
        grid.setPosCircle(pos.getX(), pos.getY());
    }

    public void setColorGrid(Color c){
        for(int i = 0; i < grid.getX(); i++){
            for(int j = 0; j < grid.getY(); j++){
                grid.getCell(i,j).changerCouleur(c);
            }
        }

    }






}
