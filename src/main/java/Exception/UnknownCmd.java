package Exception;


public class UnknownCmd extends Exception {
    public UnknownCmd(String errorMessage) {
        super(errorMessage);
    }
}