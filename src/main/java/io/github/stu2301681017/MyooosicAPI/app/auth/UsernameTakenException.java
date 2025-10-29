package io.github.stu2301681017.MyooosicAPI.app.auth;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
}
