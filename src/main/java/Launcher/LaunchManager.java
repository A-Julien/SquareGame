package Launcher;

import Configuration.RmqConfig;
import FX.Manager.MenuSelectionZone;
import Manager.Manager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LaunchManager extends Application {
    private Manager manager;

    public void start(Stage primaryStage) {
        Parameters p = this.getParameters();
        if(p.getRaw().size() > 1 && p.getRaw().get(0).equals("-ip")) manager = new Manager(20, p.getRaw().get(1));
        else manager = new Manager(20, RmqConfig.RMQ_SERVER_IP);

        Screen screen = Screen.getPrimary();
        Rectangle2D ecran = screen.getVisualBounds();

        //Scene s = new Scene(grid, ecran.getWidth()/2, ecran.getHeight()/2);
        MenuSelectionZone menuSelectionZone = new MenuSelectionZone(ecran.getWidth()/4*3, ecran.getHeight()/4*3, manager);
        primaryStage.setTitle("SquaregGame - Julien ALAIMO - Olivier HUREAU");
        primaryStage.show();
        primaryStage.setScene(menuSelectionZone);

        //primaryStage.setFullScreen(true);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
