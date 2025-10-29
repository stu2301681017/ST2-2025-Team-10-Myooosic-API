package io.github.stu2301681017.MyooosicAPI.app.auth;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
