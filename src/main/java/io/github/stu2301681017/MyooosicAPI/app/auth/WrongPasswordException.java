package io.github.stu2301681017.MyooosicAPI.app.auth;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
