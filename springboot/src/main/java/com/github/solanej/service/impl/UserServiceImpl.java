package com.github.solanej.service.impl;

import com.github.solanej.entity.User;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User createUserByOpenid(String openid) {
        final User newUser = new User();
        newUser.setSex(-1);
        newUser.setOpenid(openid);
        newUser.setUid(UUID.randomUUID().toString());
        userMapper.insert(newUser);
        return newUser;
    }
}
