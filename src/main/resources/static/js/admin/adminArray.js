//이헌복 - 테이블 정렬 관련 기능
document.addEventListener('DOMContentLoaded', function() {
    // 정렬 상태를 저장할 객체
    let sortState = {
        column: null,
        direction: null  // 정렬 방향 (asc, desc)
    };

    // 정렬 가능한 모든 헤더(th.sortable)에 이벤트 리스너 추가
    document.querySelectorAll('th.sortable').forEach(header => {
        header.addEventListener('click', function(e) {
            const sortKey = this.dataset.sort;  // 클릭된 헤더의 data-sort 속성 값 (정렬 기준)
            const clickedArrow = e.target.closest('span'); // 클릭된 요소가 화살표(span)인지 확인
            
            if (clickedArrow) {
                // 화살표를 직접 클릭한 경우
                const isUpArrow = clickedArrow.classList.contains('sort-up'); // 클릭한 화살표가 위쪽 화살표인지 확인
                updateSortState(sortKey, isUpArrow ? 'asc' : 'desc'); // 방향 설정 (asc: 오름차순, desc: 내림차순)
            } else {
                // 헤더 영역을 클릭한 경우
                if (sortState.column !== sortKey) {
                    // 새로운 컬럼 클릭 시 오름차순으로 시작
                    updateSortState(sortKey, 'asc');
                } else {
                     // 이미 정렬된 열을 클릭한 경우 방향 전환
                    updateSortState(sortKey, sortState.direction === 'asc' ? 'desc' : 'asc');
                }
            }
            
            sortTable(sortKey, sortState.direction); // 테이블 정렬 실행
        });
    });

    // 정렬 상태 업데이트 및 아이콘 변경
    function updateSortState(column, direction) {
		// 정렬 상태 객체를 업데이트
		sortState.column = column;
        sortState.direction = direction;
        
        // 모든 화살표 초기화
        document.querySelectorAll('.sort-icon span').forEach(arrow => {
            arrow.classList.remove('active');
        });
        
        // 현재 정렬 열의 화살표 활성화
        const currentHeader = document.querySelector(`th[data-sort="${column}"]`);
        if (currentHeader) {
            const arrow = direction === 'asc' 
                ? currentHeader.querySelector('.sort-up')
                : currentHeader.querySelector('.sort-down');
            if (arrow) arrow.classList.add('active');  // 활성화 스타일 추가
        }
    }

    // 테이블 정렬 함수
    function sortTable(column, direction) {
        const tbody = document.querySelector('table tbody'); // 테이블 본문 요소
        const rows = Array.from(tbody.querySelectorAll('tr')); // 모든 행을 배열로 변환
		// 행 정렬
        const sortedRows = rows.sort((a, b) => {
			// 각 행에서 정렬 기준 열의 값을 가져옴
            const aValue = a.querySelector(`td[data-${column}]`)?.textContent || '';
            const bValue = b.querySelector(`td[data-${column}]`)?.textContent || '';

            // 날짜 형식인 경우 (날짜는 문자열 비교 대신 Date 객체로 변환 후 비교)
            if (column === 'date') {
                return direction === 'asc' 
                    ? new Date(aValue) - new Date(bValue)
                    : new Date(bValue) - new Date(aValue);
            }

             // 일반 텍스트인 경우 (localeCompare를 사용한 문자열 비교)
            return direction === 'asc'
                ? aValue.localeCompare(bValue)
                : bValue.localeCompare(aValue);
        });

        // 정렬된 행을 테이블 본문에 다시 추가
        tbody.innerHTML = ''; // 기존 행 제거
        sortedRows.forEach(row => tbody.appendChild(row)); // 정렬된 행 추가
    }
});