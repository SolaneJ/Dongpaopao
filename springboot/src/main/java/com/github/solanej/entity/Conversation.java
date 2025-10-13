package com.github.solanej.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Conversation {

    private String cid;

    private String oid;

    private List<String> participants;

    private Integer messageCounts;

    private LocalDateTime createTime;

    private String lastMessageContent;

    private LocalDateTime lastMessageSendTime;

    private LocalDateTime lastMessageReadTime;

    private String status;
}
