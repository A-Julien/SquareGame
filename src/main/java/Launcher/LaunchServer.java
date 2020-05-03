package Launcher;

import Configuration.RmqConfig;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

public class LaunchServer extends Application implements RmqConfig {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Server server = new Server(RPC_QUEUE_NAME, RMQ_SERVER_IP , "NoCare");
        primaryStage.setTitle("Serveur");
        primaryStage.setScene(server);
        server.run();
    }
}
