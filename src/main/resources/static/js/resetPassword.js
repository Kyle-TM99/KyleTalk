$(function() {
    $('#resetPasswordForm').on('submit', function(e) {
        e.preventDefault();
        
        if ($('#newPassword').val() !== $('#confirmPassword').val()) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        $.ajax({
            url: '/resetPassword',
            type: 'POST',
            data: {
                token: $('#token').val(),
                newPassword: $('#newPassword').val()
            },
            success: function(response) {
                if (response.status === 'success') {
                    alert(response.message);
                    window.location.href = '/loginForm';
                } else {
                    alert(response.message);
                }
            },
            error: function() {
                alert('비밀번호 재설정 중 오류가 발생했습니다.'); 
            }
        });
    });
}); 