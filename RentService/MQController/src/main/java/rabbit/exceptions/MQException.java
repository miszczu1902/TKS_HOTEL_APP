package rabbit.exceptions;

public class MQException extends RuntimeException {
    public MQException(Throwable cause) {
        super(cause);
    }
}
