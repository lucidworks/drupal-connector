package com.lucidworks.fusion.connector.exception;

public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable t) {
        super(message, t);
    }

    public ServiceException(Throwable t) {
        super(t);
    }

}
