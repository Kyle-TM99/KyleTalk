// 게시판 관련 함수들
function loadBoardList() {
    $.ajax({
        url: `/api/chat/board/${roomId}`,
        type: 'GET',
        success: function(boards) {
            const boardList = $('#boardList');
            boardList.empty();
            
            boards.forEach(board => {
                const date = new Date(board.joinedAt).toLocaleDateString();
                boardList.append(`
                    <tr onclick="viewBoard(${board.boardId})">
                        <td>${board.boardTitle}</td>
                        <td>
                            <img src="${board.writerImage}" class="rounded-circle me-2" width="24" height="24">
                            ${board.writerNickname}
                        </td>
                        <td>${date}</td>
                    </tr>
                `);
            });
        }
    });
}

function openBoardModal() {
    $('#boardModal').modal('show');
}

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
                $('#boardModal').modal('hide');
                $('#boardForm')[0].reset();
                loadBoardList();
            } else {
                alert(response.message || '게시글 작성에 실패했습니다.');
            }
        }
    });
}

function viewBoard(boardId) {
    $.ajax({
        url: `/api/chat/board/detail/${boardId}`,
        type: 'GET',
        success: function(board) {
            // 게시글 상세 보기 모달 표시
            $('#boardTitle').val(board.boardTitle);
            $('#boardContent').val(board.boardContent);
            $('#boardModal').modal('show');
        }
    });
}

// 탭 변경 이벤트 처리
$('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
    if (e.target.getAttribute('data-bs-target') === '#boardTab') {
        loadBoardList();
    } else if (e.target.getAttribute('data-bs-target') === '#calendarTab') {
        calendar.render();
    }
});

// 페이지 로드 시 초기화
$(document).ready(function() {
    // 캘린더 초기화
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        locale: 'ko',
        events: function(info, successCallback, failureCallback) {
            $.ajax({
                url: `/api/chat/calendar/${roomId}`,
                type: 'GET',
                success: function(events) {
                    successCallback(events);
                }
            });
        }
    });
    calendar.render();
}); 