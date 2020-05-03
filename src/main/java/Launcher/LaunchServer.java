package Launcher;

import Configuration.RmqConfig;
import FX.Console;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

public class LaunchServer extends Application implements RmqConfig {
    private static String ip = null;

    @Override
    public void start(Stage primaryStage) {
        Console console = new Console();
        primaryStage.setTitle("Serveur");
        primaryStage.setScene(console);
        primaryStage.show();

        new Server(RPC_QUEUE_NAME, ip, console).run();
    }
    public static void main(String[] args) {
        ip = args[0];
        launch(args);
    }
}
