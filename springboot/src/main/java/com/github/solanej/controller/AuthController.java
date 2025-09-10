package com.github.solanej.controller;

import com.github.solanej.common.R;
import com.github.solanej.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "认证接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 微信小程序登录换取用户身份
     *
     * @param code 登录凭证（code）
     * @return 登录结果
     */
    @GetMapping("/login")
    @ApiOperation("微信小程序登录换取用户身份")
    public R login(@NotNull @RequestParam("code") String code) {
        return authService.login(code);
    }
}
