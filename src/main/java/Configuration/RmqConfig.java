package Configuration;

public interface RmqConfig {
    String RMQ_SERVER_IP = "hureau.biz";
    String RPC_QUEUE_NAME = "rpc_queue_init";
    String POOL_CLIENT_QUEUE = "new_client";
    String INITMAP_EXCHANGE = "INITMAP";
    String BROADCAST_EXCHANGE = "BROADCAST";
}
