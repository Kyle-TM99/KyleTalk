package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class Friend {
    private String memberId;
    private String nickname;
    private String memberImage;
    private String statusMessage;
    private boolean online;
    private Timestamp friendsSince;
} 