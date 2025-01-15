//이헌복- 페이지별 필터 설정 관련
const filterConfigs = {
    '/members': {
        template: `
            <form id="filterForm">
                <div class="mb-3">
                    <label class="form-label">회원유형</label>
                    <div class="d-flex gap-2">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="memberType" value="beginner">
                            <label class="form-check-label">초보자</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="memberType" value="pro">
                            <label class="form-check-label">전문가</label>
                        </div>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">성별</label>
                    <div class="d-flex gap-2">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="gender" value="male">
                            <label class="form-check-label">남성</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="gender" value="female">
                            <label class="form-check-label">여성</label>
                        </div>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">스택</label>
                    <div class="d-flex flex-column gap-1">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="0-10">
                            <label class="form-check-label">10 이하</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="10-20">
                            <label class="form-check-label">10-20</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="20-30">
                            <label class="form-check-label">20-30</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="30-40">
                            <label class="form-check-label">30-40</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="40-50">
                            <label class="form-check-label">40-50</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="stack" value="50+">
                            <label class="form-check-label">50 이상</label>
                        </div>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">상태</label>
                    <div class="d-flex gap-2">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="memberType" value="active">
                            <label class="form-check-label">활성화</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="memberType" value="inactive">
                            <label class="form-check-label">비활성화</label>
                        </div>
                    </div>
                </div>
                <div class="d-grid">
                    <button type="submit" class="btn btn-primary btn-sm">필터 적용</button>
                </div>
            </form>`
    },
    // 
};

// 필터 초기화 함수
// initializeFilterForm - 필터 폼이 제출되면 필터 데이터를 수집하여 applyFilters 함수 호출
function initializeFilterForm() {
    const filterForm = document.getElementById('filterForm');
    if (!filterForm) return;

    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        const filters = {};
        // 체크박스가 여러개이기 때문에 key-value 형식의 객체로 저장
        for (let [key, value] of formData.entries()) {
            if (!filters[key]) {
                filters[key] = [];
            }
            filters[key].push(value);
        }
        
        applyFilters(filters);
    });
}

// 필터 적용 함수 
// applyFilters - 선택된 필터를 URL의 파라미터로 변환하여 콘텐츠 로드 	
function applyFilters(filters) {
	// URLSearchParams - 필터 데이터를 url 쿼리 문자열로 변환
    const params = new URLSearchParams();
    
    for (let [key, values] of Object.entries(filters)) {
        values.forEach(value => {
            if (value) {
                params.append(key, value);
            }
        });
    }
    
    const queryString = params.toString();
    const currentPath = window.location.pathname;
	// loadContent - 필터가 적용된 URL을 추가하여 콘텐츠 로드
    loadContent(`${currentPath}?${queryString}`);
}

// 필터 렌더링 함수
// renderFilter - URL을 기반으로 필터를 렌더링하고 필터 폼에 initializeFilterForm을 호출
function renderFilter(url) {
    const baseUrl = url.split('?')[0];
	// filterConfig - URL에 해당하는 필터 템플릿을 가져옴
    const filterConfig = filterConfigs[baseUrl];
    
    if (filterConfig) {
        const filterContent = document.getElementById('dynamicFilterContent');
        if (filterContent) { // innerHTML을 사용해 필터 콘텐츠 삽입
            filterContent.innerHTML = filterConfig.template;
            initializeFilterForm();
        }
    }
} 