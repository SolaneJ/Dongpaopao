package com.github.solanej.service;

import com.github.solanej.common.R;
import com.github.solanej.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService{

    R updateUserInfo(User user);

    R getUserInfo(String uId);

    R uploadAvatar(MultipartFile file, String uid);
}
