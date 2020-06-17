package com.lucidworks.fusion.connector.exception;

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
