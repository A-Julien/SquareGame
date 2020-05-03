package Exception;


public class ClientActionError extends Exception {
    public ClientActionError(String errorMessage) {
        super(errorMessage);
    }
}