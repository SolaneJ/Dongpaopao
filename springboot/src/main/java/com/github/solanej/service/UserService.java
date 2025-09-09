package com.github.solanej.service;

import com.github.solanej.entity.User;
import jakarta.validation.constraints.NotNull;

public interface UserService{

    User createUserByOpenid(@NotNull(message = "openid不能为空") String openid);
}
