package com.github.solanej.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    List<JSONObject> listConversation(@Param("uid") String uid);
}
