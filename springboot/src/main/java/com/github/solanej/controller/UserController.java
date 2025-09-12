package com.github.solanej.controller;

import com.github.solanej.common.R;
import com.github.solanej.entity.User;
import com.github.solanej.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户接口")
public class UserController {

    private final UserService userService;

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    public R updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public R getUserInfo(@RequestParam("uid") String uId) {
        return userService.getUserInfo(uId);
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    @PostMapping("/avatar")
    @ApiOperation("上传头像")
    public R uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("uid") String uid) {
        return userService.uploadAvatar(file, uid);
    }
}
