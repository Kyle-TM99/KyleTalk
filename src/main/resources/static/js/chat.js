// 게시글 작성 모달 열기
function openBoardModal() {
    const modal = new bootstrap.Modal(document.getElementById('writeBoardModal'));
    modal.show();
}

// 게시글 작성 제출
function submitBoard() {
    const boardData = {
        roomId: roomId,
        memberId: currentUser.memberId,
        boardTitle: $('#boardTitle').val(),
        boardContent: $('#boardContent').val()
    };
    
    $.ajax({
        url: '/api/chat/board',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(boardData),
        success: function(response) {
            if (response.success) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('writeBoardModal'));
                modal.hide();
                loadBoardList();
                $('#boardTitle').val('');
                $('#boardContent').val('');
            } else {
                alert(response.error || '게시글 작성에 실패했습니다.');
            }
        },
        error: function() {
            alert('게시글 작성 중 오류가 발생했습니다.');
        }
    });
} 