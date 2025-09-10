package com.github.solanej.service;

import com.github.solanej.common.R;
import com.github.solanej.entity.User;

public interface UserService{

    R updateUserInfo(User user);

    R getUserInfo(String uId);
}
