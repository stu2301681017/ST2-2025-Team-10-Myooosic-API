package io.github.stu2301681017.MyooosicAPI.app.ai;

import io.github.stu2301681017.MyooosicAPI.core.ServiceResponseException;

public class AIResponseException extends ServiceResponseException {
    public AIResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
