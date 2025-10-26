package io.github.stu2301681017.MyooosicAPI.app.ai;

import io.github.stu2301681017.MyooosicAPI.core.ServiceUnavailableException;

public class AIUnavailableException extends ServiceUnavailableException {
    public AIUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
