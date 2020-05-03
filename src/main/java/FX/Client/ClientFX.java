package FX.Client;

import Client.ClientService;
import Configuration.FxConfig;
import FX.Console;
import FX.Map.Grid;
import Manager.Map.Cell;
import Configuration.RmqConfig;
import Utils.Direction;
import Utils.Logger.SimpleLogger;
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
    
    private SimpleLogger logger;

    private Console console;

    /**
     * Init FX action handler and launch FX for client.
     * Start Client Services
     *
     * @param width
     * @param height
     * @throws IOException
     * @throws TimeoutException
     */
    public ClientFX(double width, double height) throws IOException, TimeoutException {
        super(new BorderPane(),  width,  height);
        this.console = new Console();
        this.logger = new SimpleLogger("CLIENT", this.console);
        this.logger.addTag("FX");


        BorderPane borderPane = (BorderPane) this.getRoot();

        this.grid = new Grid(FxConfig.height,FxConfig.width,width/2*0.85,width/2*0.85, false);

        this.pos = new Cell(0,0);

        BorderPane topBorder = new BorderPane();
        topBorder.setCenter(grid);
        borderPane.setTop(topBorder);
        borderPane.setCenter(console.getScrollPane());

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
             direction = Direction.LEFT;
            } else if(key.getCode()== KeyCode.D || key.getCode()== KeyCode.RIGHT) {
                direction = Direction.RIGHT;
            } else if(key.getCode()== KeyCode.Z || key.getCode()== KeyCode.UP) {
                direction = Direction.UP;
            } else if(key.getCode()== KeyCode.S || key.getCode()== KeyCode.DOWN) {
                direction = Direction.DOWN;
                p.setY(1);
            } else {
                return;
            }
            this.logger.log("Moving " + direction.toString());

            try {
                this.clientService.handleMovement(direction);
            } catch (IOException e) {
                this.logger.log("Can not comunicate with server ");
                e.printStackTrace();
            }

        };

        this.addEventFilter(KeyEvent.KEY_RELEASED, clavier);
        this.grid.affCircle();
        this.clientService = new ClientService(this);
        clientService.setConsole(console);

    }

    /**
     * Show alert when client send hello
     */
    public void showAlert2(){
        Platform.runLater(alert2::show);
    }

    /**
     * Show response when client send hello
     */
    public void showAlert1(){
        Platform.runLater(alert1::show);
    }

    /**
     * Move circle on map
     *
     * @param cell position to move
     */
    public void moveCircle(Cell cell){
        pos = cell;
        grid.setPosCircle(pos.getX(), pos.getY());
    }

    /**
     * Set circle to a position
     * @param x coord
     * @param y coord
     */
    public void setPosCircle(int x, int y){
        this.grid.setPosCircle(x, y);
    }

    /**
     * change grid color chen client change server
     * @param color the color
     */
    public void setColorGrid(javafx.scene.paint.Color color){
        for(int i = 0; i < grid.getX(); i++){
            for(int j = 0; j < grid.getY(); j++){
                grid.getCell(i,j).colorSwap(color);
            }
        }

    }
}
