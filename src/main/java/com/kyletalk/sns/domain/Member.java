package com.kyletalk.sns.domain;

import java.sql.Date;
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
    private Integer memberNo;
    private String name;
    private String memberId;
    private String pass;
    private String nickname;
    private Date birth;
    private String gender;
    private String zipcode;
    private String address;
    private String address2;
    private String email;
    private boolean emailGet;
    private String phone;
    private Timestamp memberRegDate;
    private Integer memberType;
    private String memberImage;
    private Timestamp withdrawalEndDate;
    private Timestamp banEndDate;
    private Integer reportedCount;
    private boolean isSocial;
    private String socialType;
    private boolean isAdmin;
}
