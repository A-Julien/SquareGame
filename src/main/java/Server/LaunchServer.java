package Server;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

public class LaunchServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue_init";
    final static Logger logger = Logger.getLogger(LaunchServer.class);

    public static void main(String[] argv) throws Exception {
        logger.info("coucou");
        //Server server = new Server(RPC_QUEUE_NAME, argv[0]);
        //Server server = new Server(RPC_QUEUE_NAME, "Balek du nom ?");
        //Server server = new Server(RPC_QUEUE_NAME, "Balek du nom ?");
        //server.waitingForClient();
    }

}
