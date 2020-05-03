package FX;

import Configuration.FxConfig;
import FX.Manager.ZoneManager;
import Manager.Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;


public class MenuSelectionZone extends Scene {
    private BorderPane borderPane;
    private ZoneManager zoneManager;

    MenuSelectionZone(double largeur, double hauteur, Manager manager){
        super(new BorderPane(),  largeur,  hauteur);
        Window primary = this.getWindow();
        borderPane = (BorderPane) this.getRoot();

        Grid grid = new Grid(FxConfig.height, FxConfig.width, hauteur * 0.95, largeur / 2, true);
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
        };

       zoneManager.getLaunchButton().setOnAction(eventLaunch);
    }
}
