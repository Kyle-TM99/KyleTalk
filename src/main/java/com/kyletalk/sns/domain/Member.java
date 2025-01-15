package com.kylemall.shop.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private String id;                 // 고유 ID
    private String name;               // 이름
    private String pass;               // 비밀번호
    private String email;              // 이메일
    private String mobile;             // 휴대폰 번호
    private String zipcode;            // 우편번호
    private String address1;           // 주소 1
    private String address2;           // 주소 2
    private boolean emailGet;          // 이메일 수신 여부
    private Timestamp regDate;     	   // 등록일
    private String nickname;           // 닉네임
    private boolean isAdmin;           // 관리자 여부
    private boolean isSocial;          // 소셜 계정 여부
    private String socialType;         // 소셜 로그인 유형 (none, kakao, google)
    private String profileImage;       // 프로필 이미지 URL
	
}