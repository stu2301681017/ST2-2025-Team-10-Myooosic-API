package io.github.stu2301681017.MyooosicAPI.core;

public class ServiceResponseException extends RuntimeException {
    public ServiceResponseException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceResponseException(String message) {
        super(message);
    }

}
