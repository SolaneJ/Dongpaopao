package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.solanej.common.R;
import com.github.solanej.config.WeixinConfig;
import com.github.solanej.entity.User;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.AuthService;
import com.github.solanej.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
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

    private final UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final TokenService tokenService;

    @Override
    public R login(String code) {
        /* 调用微信登录Code2Session接口 */
        final String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";

        /* 构建请求参数 */
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

        User theUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
                        .select(User::getUid)
                        .select(User::getNickname)
                        .select(User::getSex)
                        .select(User::getSid)
                        .select(User::getAvatar)
                        .select(User::getPhone)
                        .select(User::getAvatar)
                        .select(User::getCtime));

        /* 没有用户则创建一条记录 */
        if (theUser == null) {
            theUser = createUserByOpenid(openid);
        }

        final String sessionKey = jsonObject.getString("session_key");
        stringRedisTemplate.opsForValue().set(theUser.getUid(), sessionKey);
        return R.success(theUser);
    }

    /**
     * @return pure phone number
     */
    @Override
    public R getPhoneNumber(String uid, String code) {
        String accessToken = tokenService.getAccessToken();

        // 构建请求URL
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;

        // 构建请求体（使用FastJSON创建JSON对象）
        JSONObject requestBody = new JSONObject();
        requestBody.put("code", code);

        // 创建HttpEntity
        HttpEntity<String> request = new HttpEntity<>(requestBody.toJSONString());

        // 发送POST请求
        JSONObject response = restTemplate.postForObject(url, request, JSONObject.class);

        String purePhoneNumber = response.getJSONObject("phone_info").getString("purePhoneNumber");

        /* 更新用户手机号 */
        userMapper.update(new LambdaUpdateWrapper<User>().eq(User::getUid, uid).set(User::getPhone, purePhoneNumber));

        return R.success(purePhoneNumber);
    }

    /**
     * 新增一条用户记录
     */
    private User createUserByOpenid(String openid) {
        final User newUser = new User();
        newUser.setSex(-1);
        newUser.setOpenid(openid);
        newUser.setUid(UUID.randomUUID().toString());
        userMapper.insert(newUser);
        return newUser;
    }
}
