package io.github.stu2301681017.MyooosicAPI.app.auth;

import io.github.stu2301681017.MyooosicAPI.core.ApiConstraints;
import io.github.stu2301681017.MyooosicAPI.core.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated({ApiConstraints.class})
public class AuthAPI {

    private final AuthService authService;

    public AuthAPI(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<Void> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        authService.register(request);
        return new ApiResponse<>(
                null,
                HttpStatus.OK,
                "Successfully registered");
    }

    @PostMapping("/register")
    public ApiResponse<Void> login(
            @RequestBody @Valid LoginRequest request
    ) {
        authService.login(request);
        return new ApiResponse<>(
                null,
                HttpStatus.OK,
                "Successfully logged in");
    }

    @PostMapping("/logoff")
    public ApiResponse<Void> logoff() {
        authService.logoff();
        return new ApiResponse<>(
                null,
                HttpStatus.OK,
                "Successfully logged in");
    }

}
