package rabbit.exceptions;

public class MQException extends RuntimeException {
    public MQException(String message) {
        super(message);
    }
}
