package Launcher;
import Configuration.RmqConfig;
import FX.Client.ClientFX;
import Manager.Manager;
import javafx.application.Application;
import javafx.stage.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// assumes the current class is called MyLogger
public class LaunchClient extends Application{
    private Manager manager;
    private static String ip;

    public void start(Stage primaryStage) throws IOException, TimeoutException {
        Screen screen = Screen.getPrimary();

        primaryStage.setTitle("SquaregGame - Julien ALAIMO - Olivier HUREAU");
        primaryStage.show();
        ClientFX clientFX = new ClientFX(800,400, RmqConfig.RMQ_SERVER_IP);
        primaryStage.setScene(clientFX);
    }


    public static void main(String[] args) {
        //ip = args[0];
        launch(args);
    }
}