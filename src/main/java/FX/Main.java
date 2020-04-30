package FX;
import Manager.Manager;
import javafx.application.Application;
import javafx.stage.*;
import javafx.geometry.*;

import java.util.logging.Logger;

// assumes the current class is called MyLogger
public class Main extends Application{
    private Manager manager;

    public void start(Stage primaryStage) {
        manager = new Manager(5);
        Screen screen = Screen.getPrimary();
        Rectangle2D ecran = screen.getVisualBounds();

      //  Scene s = new Scene(grid, ecran.getWidth()/2, ecran.getHeight()/2);
        MenuSelectionZone menuSelectionZone = new MenuSelectionZone(ecran.getWidth()/4*3, ecran.getHeight()/4*3, manager);
        primaryStage.setTitle("SquaregGame - Julien ALAIMO - Olivier HUREAU");
        primaryStage.show();
        primaryStage.setScene(menuSelectionZone);

       // primaryStage.setFullScreen(true);

    }


    public static void main(String[] args) {
        launch(args);
    }

 }