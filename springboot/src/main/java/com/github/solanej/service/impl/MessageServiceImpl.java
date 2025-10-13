package com.github.solanej.service.impl;

import com.github.solanej.mapper.MessageMapper;
import com.github.solanej.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
}

