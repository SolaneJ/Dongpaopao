package com.github.solanej.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {

    private String mid;

    private String cid;

    private String sender;

    private String receiver;

    private enum type {
        TEXT, VOICE, IMAGE
    }

    private String content;

    private enum status {
        SENDING, SENT, READ, FAILED
    }

    private LocalDateTime sendTime;

    private LocalDateTime readTime;
}

