package com.github.solanej.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class WeixinConfig {

    @Value("${WECHAT_MINI_PROGRAM_APP_ID}")
    private String appId;

    @Value("${WECHAT_MINI_PROGRAM_APP_SECRET}")
    private String appSecret;
}
