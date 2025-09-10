package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.config.WeixinConfig;
import com.github.solanej.entity.User;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.AuthService;
import com.github.solanej.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WeixinConfig weixinConfig;

    private final RestTemplate restTemplate;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    public R login(String code) {
        // 调用微信登录Code2Session接口
        final String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";

        // 构建请求参数
        final Map<String, String> params = new HashMap<>();
        params.put("appid", weixinConfig.getAppId());
        params.put("secret", weixinConfig.getAppSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        final JSONObject jsonObject = JSONObject.parseObject(
                restTemplate.getForEntity(url, String.class, params).getBody());

        if (jsonObject.containsKey("errcode")) {
            return R.error("非法请求!");
        }

        /* 用户信息只用一次openid用来换取uId */
        final String openid = jsonObject.getString("openid");

        User theUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (theUser == null) {
            theUser = createUserByOpenid(openid);
        }

        return R.success(theUser.getUid());
    }

    private User createUserByOpenid(String openid) {
        final User newUser = new User();
        newUser.setSex(-1);
        newUser.setOpenid(openid);
        newUser.setUid(UUID.randomUUID().toString());
        userMapper.insert(newUser);
        return newUser;
    }
}
