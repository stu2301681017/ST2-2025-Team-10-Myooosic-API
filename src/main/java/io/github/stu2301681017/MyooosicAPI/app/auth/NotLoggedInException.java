package io.github.stu2301681017.MyooosicAPI.app.auth;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String message) {
        super(message);
    }
}
