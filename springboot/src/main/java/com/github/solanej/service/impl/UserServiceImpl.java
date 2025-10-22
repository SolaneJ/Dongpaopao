package com.github.solanej.service.impl;

import com.github.solanej.common.R;
import com.github.solanej.config.OSSConfig;
import com.github.solanej.entity.User;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final S3Client s3Client;

    @Override
    public R updateUserInfo(User user) {
        userMapper.updateById(user);
        return R.success();
    }

    @Override
    public R getUserInfo(String uId) {
        User user = userMapper.selectById(uId);
        return R.success(user);
    }

    @Override
    @SneakyThrows
    public R uploadAvatar(MultipartFile file, String uid) {
        // 获取流
        RequestBody body = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        String uuid = UUID.randomUUID().toString();

        PutObjectResponse avatar = s3Client.putObject(builder ->
                builder.contentType("image/jpeg").bucket("avatar").key(uuid), body);

        /* 当前版保存头像的策略是直接取文件名(uuid)放入桶中，所以在数据库更新头像时直接放入[http://host:port/avatar/uuid]即可 */
        String avatarUrl = OSSConfig.ENDPOINT + "avatar/" + uuid;

        final User user = new User();
        user.setUid(uid);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        return R.success(avatarUrl);
    }
}
