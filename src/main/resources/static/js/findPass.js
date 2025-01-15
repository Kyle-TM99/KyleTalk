$(function() {
    $('#findPassForm').on('submit', function(e) {
        e.preventDefault();
        
        $.ajax({
            url: '/findPass',
            type: 'POST',
            data: {
                memberId: $('#memberId').val(),
                email: $('#email').val()
            },
            success: function(response) {
                if (response.status === 'success') {
                    alert(response.message);
                    window.location.href = '/loginForm'; // 로그인 페이지로 리다이렉트
                } else {
                    alert(response.message);
                }
            },
            error: function() {
                alert('비밀번호 찾기 처리 중 오류가 발생했습니다.');
            }
        });
    });
}); 