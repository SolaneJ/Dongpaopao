package com.github.solanej.service.impl;

import com.github.solanej.mapper.ConversationMapper;
import com.github.solanej.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;
}