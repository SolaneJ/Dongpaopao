package com.github.solanej.service.impl;

import com.github.solanej.ResponseData;
import com.github.solanej.constant.WeChat;
import com.github.solanej.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;

    @Override
    public ResponseData login(String code) {
        System.out.println(WeChat.APP_ID);
        System.out.println(WeChat.APP_SECRET);
        System.out.println(code);
        return ResponseData.success();
    }
}
