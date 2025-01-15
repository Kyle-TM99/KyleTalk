// 카카오 회원 추가정보용 JS
$(function() {
    // 정규식 패턴 정의
    const patterns = {
        memberId: /^[a-zA-Z0-9]{4,12}$/,
        password: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,16}$/,
        nickname: /^[A-Za-z0-9가-힣]{2,8}$/,
        phone: /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/
    };

    // 아이디 유효성 검사
    $("#socialMemberId").on("input", function() {
        const memberId = $(this).val().trim();
        if (!patterns.memberId.test(memberId)) {
            $("#socialMemberIdMessage").text("4~12자의 영문, 숫자만 사용 가능합니다.").css("color", "red");
            return;
        }
        
        // 아이디 중복 검사
        $.ajax({
            url: "/checkId",
            type: "POST",
            data: { memberId: memberId },
            success: function(response) {
                if (response.available) {
                    $("#socialMemberIdMessage").text("사용 가능한 아이디입니다.").css("color", "green");
                } else {
                    $("#socialMemberIdMessage").text("이미 사용 중인 아이디입니다.").css("color", "red");
                }
            }
        });
    });

    // 비밀번호 유효성 검사
    $("#socialPass").on("input", function() {
        const password = $(this).val();
        if (!patterns.password.test(password)) {
            $("#socialPwMessage").text("8~16자의 영문, 숫자, 특수문자를 모두 포함해야 합니다.").css("color", "red");
        } else {
            $("#socialPwMessage").text("사용 가능한 비밀번호입니다.").css("color", "green");
        }
    });

    // 비밀번호 확인
    $("#socialPwCheck").on("input", function() {
        const password = $("#socialPass").val();
        const passwordCheck = $(this).val();
        
        if (password === passwordCheck) {
            $("#socialPwMatchMessage").text("비밀번호가 일치합니다.").css("color", "green");
        } else {
            $("#socialPwMatchMessage").text("비밀번호가 일치하지 않습니다.").css("color", "red");
        }
    });

    // 닉네임 유효성 검사
    $("#socialNickname").on("input", function() {
        const nickname = $(this).val().trim();
        if (!patterns.nickname.test(nickname)) {
            $("#socialNicknameMessage").text("2~8자의 한글, 영문, 숫자만 사용 가능합니다.").css("color", "red");
            return;
        }
        
        // 닉네임 중복 검사
        $.ajax({
            url: "/checkNickname",
            type: "POST",
            data: { nickname: nickname },
            success: function(response) {
                if (response.available) {
                    $("#socialNicknameMessage").text("사용 가능한 닉네임입니다.").css("color", "green");
                } else {
                    $("#socialNicknameMessage").text("이미 사용 중인 닉네임입니다.").css("color", "red");
                }
            }
        });
    });

    // 전화번호 형식 자동 변환
    $("#socialPhone").on("input", function() {
        let number = $(this).val().replace(/[^0-9]/g, "").substr(0, 11);
        if (number.length > 3) {
            number = number.length > 7 
                ? `${number.substr(0,3)}-${number.substr(3,4)}-${number.substr(7)}`
                : `${number.substr(0,3)}-${number.substr(3)}`;
        }
        $(this).val(number);
        
        // 전화번호 유효성 검사
        if (!patterns.phone.test(number)) {
            $("#socialPhoneMessage").text("올바른 전화번호 형식이 아닙니다.").css("color", "red");
            return;
        }
        
        // 전화번호 중복 검사
        $.ajax({
            url: "/checkPhone",
            type: "POST",
            data: { phone: number },
            success: function(response) {
                if (response.available) {
                    $("#socialPhoneMessage").text("사용 가능한 전화번호입니다.").css("color", "green");
                } else {
                    $("#socialPhoneMessage").text("이미 등록된 전화번호입니다.").css("color", "red");
                }
            }
        });
    });

    // 주소 검색
    $("#socialAddressSearchBtn").on("click", function() {
        new daum.Postcode({
            oncomplete: function(data) {
                $("#socialZipcode").val(data.zonecode);
                $("#socialAddress").val(data.address);
                $("#socialAddress2").focus();
            }
        }).open();
    });

    // 폼 제출
    $("#addJoinForm").on("submit", function(e) {
        e.preventDefault();
        
        // 모든 필수 필드 검증
        const requiredFields = [
            { id: "socialMemberId", message: "아이디를 입력해주세요." },
            { id: "socialPass", message: "비밀번호를 입력해주세요." },
            { id: "socialPwCheck", message: "비밀번호 확인을 입력해주세요." },
            { id: "socialNickname", message: "닉네임을 입력해주세요." },
            { id: "socialBirth", message: "생년월일을 입력해주세요." },
            { id: "socialPhone", message: "전화번호를 입력해주세요." },
            { id: "socialZipcode", message: "우편번호를 입력해주세요." },
            { id: "socialAddress", message: "주소를 입력해주세요." },
            { id: "socialAddress2", message: "상세주소를 입력해주세요." }
        ];

        for (const field of requiredFields) {
            const value = $(`#${field.id}`).val().trim();
            if (!value) {
                alert(field.message);
                $(`#${field.id}`).focus();
                return;
            }
        }

        // 폼 데이터 전송
        $.ajax({
            url: "/addJoin",
            type: "POST",
            data: $(this).serialize(),
            success: function(response) {
                if (response.status === "success") {
                    alert("회원가입이 완료되었습니다.");
                    window.location.href = "/loginForm";
                } else {
                    alert(response.message || "회원가입에 실패했습니다. 다시 시도해주세요.");
                }
            },
            error: function() {
                alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
        });
    });
}); 