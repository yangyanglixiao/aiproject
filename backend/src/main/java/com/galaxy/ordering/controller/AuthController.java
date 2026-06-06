package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.LoginRequest;
import com.galaxy.ordering.dto.LoginResponse;
import com.galaxy.ordering.dto.RegisterRequest;
import com.galaxy.ordering.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.ok("注册成功");
    }
}
