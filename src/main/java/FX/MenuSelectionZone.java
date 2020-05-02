package FX;

import FX.Manager.ZoneManager;
import Manager.Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MenuSelectionZone extends Scene {
    int nbJoueur = 0;
    BorderPane borderPane;
    Grid grid;
    ZoneManager zoneManager;
    Button newPlayer;
    Window primary;
    MenuSelectionZone(double largeur, double hauteur, Manager manager){
        super(new BorderPane(),  largeur,  hauteur);
        primary = this.getWindow();
        borderPane = (BorderPane) this.getRoot();

        grid = new Grid(10,10,hauteur*0.95,largeur/2, true);
        borderPane.setCenter(grid);
        zoneManager = new ZoneManager(grid, manager);

        borderPane.setRight(zoneManager);

        grid.setCurrentZone(zoneManager.getCurrentZone());



        EventHandler<ActionEvent> eventLaunch= e -> {
            zoneManager.eventLaunch();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Initialisation");
            alert.setHeaderText("Initialisation réussi");
            alert.setContentText("Tout les serveurs se sont bien connecté");
            borderPane.setRight(null);

            alert.showAndWait();
            //grid.affCircle();
        };


       zoneManager.getLaunchButton().setOnAction(eventLaunch);
    }
}
