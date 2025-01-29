package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class FriendRequest {
    private String requestId;
    private String fromMemberId;
    private String toMemberId;
    private String nickname;
    private String memberImage;
    private Timestamp requestDate;
} 