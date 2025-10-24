package com.github.solanej.service;

import com.github.solanej.common.R;
import com.github.solanej.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface UserService{

    R updateUserInfo(User user);

    R getUserInfo(String uId);

    R getBalance(String uid);

    R uploadAvatar(MultipartFile file, String uid);

    R recharge(String uid, BigDecimal amount);

     R withdraw(String uid, BigDecimal amount);
}
