package com.github.solanej.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.Conversation;
import com.github.solanej.mapper.ConversationMapper;
import com.github.solanej.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;

    @Override
    public R listConversation(String uid) {
        List<Objects> result = conversationMapper.selectObjs(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getStatus, "OPENING")
                .like(Conversation::getParticipants, uid)
                .orderByDesc(Conversation::getLastMessageSendTime));
        return R.success(result);
    }
}