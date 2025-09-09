package com.github.solanej.controller;

import com.github.solanej.common.R;
import com.github.solanej.service.AuthService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public R login(@NotNull @RequestParam("code") String code) {
        return authService.login(code);
    }
}
