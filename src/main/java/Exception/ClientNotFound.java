package Exception;


public class ClientNotFound extends Exception {
    public ClientNotFound(String errorMessage) {
        super(errorMessage);
    }
}