package Class;


public class InformationsServeur {
    String queue;
    String queueClient;
    public InformationsServeur(String s){
        this.queueClient = s;
    }

    public String getQueueClient() {
        return queueClient;
    }
}
