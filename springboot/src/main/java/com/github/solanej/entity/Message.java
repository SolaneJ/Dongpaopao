package com.github.solanej.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {

    private String mid;

    private String cid;

    private String sender;

    private String receiver;

    private String type;

    private String content;

    private String status;

    private LocalDateTime sendTime;

    private LocalDateTime readTime;
}

