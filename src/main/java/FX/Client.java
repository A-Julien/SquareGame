package FX;

import Manager.Map.Cell;
import Configuration.RmqConfig;
import Task.Deplacement;
import Task.Task;
import Task.TaskCommand;
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



public class Client extends Scene implements RmqConfig {
    BorderPane borderPane;
    Grid grid;
    Channel envoyerInformation;

    Cell pos;
    int i;
    boolean synchro;

    private static final String TASK_QUEUE_NAME = "task_queue";
    private static String queuServer;
    private static String myQueue;


    public Client(double largeur, double hauteur) throws IOException, TimeoutException {
        super(new BorderPane(),  largeur,  hauteur);
        borderPane = (BorderPane) this.getRoot();

        grid = new Grid(10,10,hauteur*0.9,largeur*0.9, false);

        this.pos = new Cell(0,0);

        borderPane.setCenter(grid);
        synchro = false;


        EventHandler<KeyEvent> clavier = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {

                Deplacement dep;

                Cell p = new Cell(0,0);

                if(key.getCode()== KeyCode.Q || key.getCode()== KeyCode.LEFT) {
                    System.out.println("Déplacement GAUCHE");
                 dep = Deplacement.GAUCHE;
                } else if(key.getCode()== KeyCode.D || key.getCode()== KeyCode.RIGHT) {
                    System.out.println("Déplacement droite");
                    dep = Deplacement.DROITE;
                } else if(key.getCode()== KeyCode.Z || key.getCode()== KeyCode.UP) {
                    System.out.println("Déplacement Haut");
                    dep = Deplacement.HAUT;
                } else if(key.getCode()== KeyCode.S || key.getCode()== KeyCode.DOWN) {
                    dep = Deplacement.BAS;
                    p.setY(1);
                } else {
                    return;
                }

                try {
                    handleMouvement(dep);
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

        envoyerInformation = connection.createChannel();
        Channel recevoirInformation = connection.createChannel();
        myQueue = recevoirInformation.queueDeclare().getQueue();


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                Task task = (Task) Communication.deserialize(delivery.getBody());
                System.out.println(" [x] New Task there'" + task.toString() + "'");

                switch (task.cmdType){
                    case INIT:
                        queuServer = task.replyQueu;
                        this.pos = (Cell) task.cmd;
                        this.grid.setPosCircle(pos.getX(), pos.getY());

                        System.out.println("On m'as assigné la position" + pos);
                        break;
                    case MOVE_GRANTED:
                        mouvement((Cell) task.cmd);




                    break;

                    case MOVE_GRANTED_FORWARDED:
                       // this.envoyerInformation.close();
                       // this.envoyerInformation = connection.createChannel();

                        queuServer = task.replyQueu;
                        mouvement((Cell) task.cmd);
                        // CHANGER COULEUR ? :)

                        break;
                    default:

                }


            } catch (ClassNotFoundException  e) {
                e.printStackTrace();
                System.out.println("Problem during task");
            }

        };
        recevoirInformation.basicConsume(myQueue, true, deliverCallback, consumerTag -> { });

        // INITIALISE LA CONNECTION
        Task TaskToSend = new Task(TaskCommand.INIT, null, myQueue);
        this.envoyerInformation.basicPublish("",POOL_CLIENT_QUEUE, null, Communication.serialize(TaskToSend));
        this.grid.affCircle();
    }






    public void handleMouvement(Deplacement mouvement) throws IOException {
        System.out.println("Déplacement souhaité : " + mouvement);
       // Cell to = new Cell(pos.getX() + mouvement.getX(), pos.getY()+mouvement.getY());
        Task TaskToSend = new Task(TaskCommand.MOVE,mouvement, myQueue);
        this.envoyerInformation.basicPublish("",queuServer, null, Communication.serialize(TaskToSend));



    }

    public void mouvement(Cell p){
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
