package ob1.eventmanager.exception;

public class EventAlreadyExistsException extends EventException {

    public EventAlreadyExistsException() {
    }

    public EventAlreadyExistsException(String message) {
        super(message);
    }

    public EventAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public EventAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
