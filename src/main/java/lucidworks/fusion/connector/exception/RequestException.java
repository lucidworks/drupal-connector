package lucidworks.fusion.connector.exception;

import lucidworks.fusion.connector.exception.ServiceException;

public class RequestException extends ServiceException {

    public RequestException() {
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable t) {
        super(message, t);
    }

    public RequestException(Throwable t) {
        super(t);
    }
}
