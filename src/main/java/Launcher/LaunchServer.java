package Launcher;

import Configuration.RmqConfig;
import FX.Console;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

public class LaunchServer extends Application implements RmqConfig {

    @Override
    public void start(Stage primaryStage) {
         Parameters p = this.getParameters();

        Console console = new Console();
        primaryStage.setTitle("Serveur");
        primaryStage.setScene(console);
        primaryStage.show();

        if(p.getRaw().size() > 1 && p.getRaw().get(0).equals("-ip")) new Server(RPC_QUEUE_NAME, p.getRaw().get(1), console).run();
        else new Server(RPC_QUEUE_NAME, RmqConfig.RMQ_SERVER_IP, console).run();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
