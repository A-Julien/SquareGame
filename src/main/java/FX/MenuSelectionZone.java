package FX;

import Manager.Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MenuSelectionZone extends Scene {
    int nbJoueur = 0;
    BorderPane borderPane;
    Grid grid;
    ZoneManager zoneManager;
    Button newPlayer;
    MenuSelectionZone(double largeur, double hauteur, Manager manager){
        super(new BorderPane(),  largeur,  hauteur);
        borderPane = (BorderPane) this.getRoot();

        grid = new Grid(10,10,hauteur*0.95,largeur/2, true);
        borderPane.setCenter(grid);
        zoneManager = new ZoneManager(grid, manager);

        borderPane.setRight(zoneManager);

        grid.setCurrentZone(zoneManager.getCurrentZone());

        newPlayer = new Button("Nouveau Joueur");
        BorderPane player = new BorderPane();
        player.setCenter(newPlayer);
        EventHandler<ActionEvent> eventLaunch= e -> {
            borderPane.setLeft(player);
            zoneManager.eventLaunch();
            //grid.affCircle();
        };

        newPlayer.setOnAction(click -> {
            nbJoueur++;
            System.out.println("Nouveau joueur");
            Stage stage = new Stage();
            try {
                stage.setScene(new Client(400,400));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            stage.setTitle("Joueur #" + nbJoueur + " - SquaregGame - Julien ALAIMO - Olivier HUREAU" );
            stage.show();
        });
       zoneManager.getLaunchButton().setOnAction(eventLaunch);
    }
}
