package com.github.solanej.controller;

import com.github.solanej.common.R;
import com.github.solanej.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/list")
    public R listConversation(@RequestParam("uid") String uid) {
        return conversationService.listConversation(uid);
    }
}