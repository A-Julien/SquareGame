package FX.Client;

import Client.ClientService;
import Configuration.FxConfig;
import FX.Map.Grid;
import Manager.Map.Cell;
import Configuration.RmqConfig;
import Utils.Direction;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.concurrent.TimeoutException;



public class ClientFX extends Scene implements RmqConfig {
    private Grid grid;
    private Alert alert1;
    private Alert alert2;

    private Cell pos;

    private ClientService clientService;


    public ClientFX(double largeur, double hauteur) throws IOException, TimeoutException {
        super(new BorderPane(),  largeur,  hauteur);

        BorderPane borderPane = (BorderPane) this.getRoot();

        this.grid = new Grid(FxConfig.height,FxConfig.width,hauteur*0.9,largeur*0.9, false);

        this.pos = new Cell(0,0);

        borderPane.setCenter(grid);

        this.alert1 = new Alert(Alert.AlertType.INFORMATION);
        this.alert1.setTitle("Regardez aux alentours !");
        this.alert1.setHeaderText("On dirais que...");
        this.alert1.setContentText("BONJOUR !");

        this.alert2 = new Alert(Alert.AlertType.INFORMATION);
        this.alert2.setTitle("Au mais..");
        this.alert2.setHeaderText("Les gens ne répondent même plus aux bonjours !");
        this.alert2.setContentText("*Regard de haine* Bonjour à vous aussi...");


        EventHandler<KeyEvent> clavier = key -> {

            Direction direction;

            Cell p = new Cell(0,0);

            if(key.getCode()== KeyCode.Q || key.getCode()== KeyCode.LEFT) {
                System.out.println("Déplacement GAUCHE");
             direction = Direction.LEFT;
            } else if(key.getCode()== KeyCode.D || key.getCode()== KeyCode.RIGHT) {
                System.out.println("Déplacement droite");
                direction = Direction.RIGHT;
            } else if(key.getCode()== KeyCode.Z || key.getCode()== KeyCode.UP) {
                System.out.println("Déplacement Haut");
                direction = Direction.UP;
            } else if(key.getCode()== KeyCode.S || key.getCode()== KeyCode.DOWN) {
                direction = Direction.DOWN;
                p.setY(1);
            } else {
                return;
            }

            try {
                this.clientService.handleMovement(direction);
            } catch (IOException e) {
                System.out.println("Impossible de communiquer avec le serveur");
                e.printStackTrace();
            }

        };

        this.addEventFilter(KeyEvent.KEY_RELEASED, clavier);
        this.grid.affCircle();
        this.clientService = new ClientService(this);

    }

    public void showAlert2(){
        Platform.runLater(alert2::show);
    }

    public void showAlert1(){
        Platform.runLater(alert1::show);
    }

    public void moveCircle(Cell p){
        pos = p;
        grid.setPosCircle(pos.getX(), pos.getY());
    }

    public void setPosCircle(int x, int y){
        this.grid.setPosCircle(x, y);
    }

    public void setColorGrid(javafx.scene.paint.Color c){
        for(int i = 0; i < grid.getX(); i++){
            for(int j = 0; j < grid.getY(); j++){
                grid.getCell(i,j).colorSwap(c);
            }
        }

    }
}
