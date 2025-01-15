package com.kyletalk.sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kyletalk.sns.domain.Member;
import com.kyletalk.sns.domain.PasswordResetToken;
import com.kyletalk.sns.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final JavaMailSender mailSender;

	// 회원 로그인 요청을 처리하고 결과를 반환하는 메서드
	public int login(String memberId, String pass) {
		int result = -1;
		Member m = memberMapper.getMember(memberId);
		if(m == null) {
			return result;
		}
		if(passwordEncoder.matches(pass, m.getPass())) {
			result = 1;
		} else { 
			result = 0;
		}
			return result;
		}
		
	// 회원 로그인 요청을 처리하기 위한 메서드
	public Member getMember(String memberId) {
		return memberMapper.getMember(memberId);
	}
	
	public int insertMember(Member member) {
		return memberMapper.insertMember(member);
	}

    public int checkMemberId(String memberId) {
        return memberMapper.checkMemberId(memberId);
    }

    public int checkNickname(String nickname) {
        return memberMapper.checkNickname(nickname);
    }
    public int checkEmail(String email) {
    	return memberMapper.checkEmail(email);
    }

    public int checkPhone(String phone) {
        return memberMapper.checkPhone(phone);
    }

    public String findMemberId(Member member) {
        
        // 아이디 찾기 실행
        return memberMapper.findMemberId(member);
    }

    public void sendPasswordResetEmail(String memberId, String email) {
        log.info("비밀번호 재설정 요청 - memberId: {}, email: {}", memberId, email);
        
        Member member = memberMapper.getMember(memberId);
        log.info("조회된 회원 정보: {}", member);
        
        if (member == null) {
            log.error("회원 정보가 없음 - memberId: {}", memberId);
            throw new RuntimeException("일치하는 회원 정보가 없습니다.");
        }
        
        if (!email.equals(member.getEmail())) {
            log.error("이메일 불일치 - 입력: {}, DB: {}", email, member.getEmail());
            throw new RuntimeException("일치하는 회원 정보가 없습니다.");
        }
        
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(memberId, token);
        
        try {
            memberMapper.savePasswordResetToken(resetToken);
            
            String resetLink = "http://localhost:8080/resetPassword?token=" + token;
            String emailContent = String.format(
                "안녕하세요,\n\n" +
                "비밀번호 재설정을 위한 링크입니다:\n%s\n\n" +
                "이 링크는 24시간 동안 유효합니다.\n" +
                "본인이 요청하지 않았다면 이 이메일을 무시하시기 바랍니다.",
                resetLink
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("rlaxoals9977@gmail.com");
            message.setTo(email);
            message.setSubject("OneStack 비밀번호 재설정");
            message.setText(emailContent);
            mailSender.send(message);
            
            log.info("비밀번호 재설정 이메일 발송 완료 - email: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패", e);
            throw new RuntimeException("비밀번호 재설정 이메일 발송에 실패했습니다.");
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = memberMapper.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new RuntimeException("유효하지 않거나 만료된 토큰입니다.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        memberMapper.updatePassword(resetToken.getMemberId(), encodedPassword);
        memberMapper.deletePasswordResetToken(token);
    }

}
