package com.github.solanej.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.User;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public R updateUserInfo(User user) {
        userMapper.updateById(user);
        return R.success();
    }

    @Override
    public R getUserInfo(String uId) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, uId));
        return R.success(user);
    }
}
