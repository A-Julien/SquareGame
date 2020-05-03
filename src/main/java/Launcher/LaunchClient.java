package Launcher;
import FX.Client.ClientFX;
import Manager.Manager;
import javafx.application.Application;
import javafx.stage.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// assumes the current class is called MyLogger
public class LaunchClient extends Application{
    private Manager manager;

    public void start(Stage primaryStage) throws IOException, TimeoutException {
        Screen screen = Screen.getPrimary();

        primaryStage.setTitle("SquaregGame - Julien ALAIMO - Olivier HUREAU");
        primaryStage.show();
        ClientFX clientFX = new ClientFX(400,400);
        primaryStage.setScene(clientFX);
    }


    public static void main(String[] args) {
        launch(args);
    }

}