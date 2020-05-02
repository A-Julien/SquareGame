package Client;
import Manager.Manager;
import javafx.application.Application;
import javafx.stage.*;
import FX.Client;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// assumes the current class is called MyLogger
public class LaunchClient extends Application{
    private Manager manager;

    public void start(Stage primaryStage) throws IOException, TimeoutException {
        Screen screen = Screen.getPrimary();

        primaryStage.setTitle("SquaregGame - Julien ALAIMO - Olivier HUREAU");
        primaryStage.show();
        Client client = new Client(400,400);
        primaryStage.setScene(client);
    }


    public static void main(String[] args) {
        launch(args);
    }

}