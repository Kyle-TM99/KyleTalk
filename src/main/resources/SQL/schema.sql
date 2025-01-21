-- Member - 회원
CREATE TABLE Member (
   member_no INTEGER AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(5) NOT NULL,
   member_id VARCHAR(50) UNIQUE NOT NULL,
   pass VARCHAR(100) NOT NULL,
   nickname VARCHAR(20) UNIQUE NOT NULL,
   birth DATE NOT NULL,
   gender VARCHAR(10) NOT NULL, 
   zipcode   VARCHAR(5) NOT NULL,
   address   VARCHAR(50) NOT NULL,
   address2 VARCHAR(50) NOT NULL,
   email VARCHAR(30) UNIQUE NOT NULL,
   email_get TINYINT DEFAULT 1 NOT NULL, -- 1(수신) 0(비수신) --
   phone VARCHAR(13) UNIQUE NOT NULL,
   member_reg_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   is_admin TINYINT(1) DEFAULT 0 NOT NULL, -- 0: 회원, 1: 관리자
   member_image VARCHAR(100) NULL,
   is_social TINYINT(1) DEFAULT 0 NOT NULL, -- 0: 일반 계정, 1: 소셜 계정
   social_type ENUM('none', 'kakao', 'google') DEFAULT 'none' NOT NULL, -- 소셜 로그인 유형
   withdrawal_end_date   TIMESTAMP NULL,
   ban_end_date   TIMESTAMP NULL,
   reported_count INTEGER DEFAULT 0 NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chat_room (
    room_id VARCHAR(50) PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    room_admin VARCHAR(50) NOT NULL,
    room_password VARCHAR(100),  -- 비공개방 비밀번호 (NULL이면 공개방)
    max_users INT DEFAULT 100,   -- 최대 참여 인원
    current_users INT DEFAULT 0, -- 현재 참여 인원
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_creator FOREIGN KEY (created_by) REFERENCES member(member_id),
    CONSTRAINT fk_room_admin FOREIGN KEY (room_admin) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chat_message (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    sender VARCHAR(50) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(10) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_room FOREIGN KEY (room_id) REFERENCES chat_room(room_id),
    CONSTRAINT fk_chat_sender FOREIGN KEY (sender) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 채팅방 참여자 테이블 추가
CREATE TABLE IF NOT EXISTS chat_room_participant (
    room_id VARCHAR(50),
    member_id VARCHAR(50),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_id, member_id),
    CONSTRAINT fk_participant_room FOREIGN KEY (room_id) REFERENCES chat_room(room_id),
    CONSTRAINT fk_participant_member FOREIGN KEY (member_id) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 채팅방 게시판 테이블 추가
CREATE TABLE IF NOT EXISTS chat_board_event (
	room_id VARCHAR(50),
    member_id VARCHAR(50),
    board_title VARCHAR(50) NOT NULL,
	board_content VARCHAR(10000) NOT NULL,
	joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_board_room FOREIGN KEY (room_id) REFERENCES chat_room(room_id),
    CONSTRAINT fk_chat_board_member FOREIGN KEY (member_id) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 채팅방 일정 테이블 추가
CREATE TABLE IF NOT EXISTS chat_calendar_event (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    all_day BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_calendar_room FOREIGN KEY (room_id) REFERENCES chat_room(room_id) ON DELETE CASCADE,
    CONSTRAINT fk_calendar_creator FOREIGN KEY (created_by) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;