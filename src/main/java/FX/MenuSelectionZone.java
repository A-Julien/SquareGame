package FX;

import Manager.Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MenuSelectionZone extends Scene {
    BorderPane borderPane;
    Grid grid;
    ZoneManager zoneManager;
    PlayerManager playerManager;
    MenuSelectionZone(double largeur, double hauteur, Manager manager){
        super(new BorderPane(),  largeur,  hauteur);
        borderPane = (BorderPane) this.getRoot();

        grid = new Grid(10,10,hauteur*0.95,largeur/2);
        borderPane.setCenter(grid);
        zoneManager = new ZoneManager(grid, manager);
        playerManager = new PlayerManager(grid);
        borderPane.setRight(zoneManager);

        grid.setCurrentZone(zoneManager.getCurrentZone());


        EventHandler<ActionEvent> eventLaunch= e -> {
            borderPane.setLeft(playerManager);
            zoneManager.eventLaunch();
            grid.affCircle();
        };
       zoneManager.getLaunchButton().setOnAction(eventLaunch);
    }
}
