package com.kyletalk.sns.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
    private Long id;
    private String memberId;
    private String token;
    private LocalDateTime expiryDate;

    public PasswordResetToken(String memberId, String token) {
        this.memberId = memberId;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(24); // 24시간 유효
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
} 