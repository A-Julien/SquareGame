package Launcher;

import Configuration.RmqConfig;
import FX.Console;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

public class LaunchServer extends Application implements RmqConfig {

    @Override
    public void start(Stage primaryStage) {
        Server server = new Server(RPC_QUEUE_NAME, RMQ_SERVER_IP);
        Console console = new Console();
        primaryStage.setTitle("Serveur");
        primaryStage.setScene(console);
        primaryStage.show();
        server.run();
    }
}
