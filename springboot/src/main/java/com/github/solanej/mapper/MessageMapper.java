package com.github.solanej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}

