package com.github.solanej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志Mapper接口
 * 继承BaseMapper，使用MyBatis-Plus提供的基础CRUD操作
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
    
}