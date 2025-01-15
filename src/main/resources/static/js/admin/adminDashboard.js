// 이헌복- 사이드바, 대시보드 관련 js
document.addEventListener('DOMContentLoaded', () => {
	// 모든 아코디언 내부 링크들을 선택
	document.querySelectorAll('.accordion-body a').forEach(link => {
		// 각 링크에 클릭 이벤트 리스너 추가
		link.addEventListener('click', function(e) {
			e.preventDefault(); // 기본 클릭 동작(페이지 이동) 막기
			const targetUrl = this.getAttribute('data-url'); // 클릭된 링크의 data-url 속성 가져오기
			loadContent(targetUrl); // 콘텐츠 로드 함수 호출
		});
	});

	// 기본 페이지 로드
	const defaultUrl = '/members';
	loadContent(defaultUrl);
});

// 콘텐츠 로드 함수
function loadContent(url) {
	fetch(url, {
		headers: { 
			'X-Requested-With': 'XMLHttpRequest',
			'Accept': 'application/json'
		}
	})
	.then(response => response.text())
	.then(html => {
		const contentArea = document.getElementById('dashboard-content');
		if (contentArea) {
			contentArea.innerHTML = html;
			renderFilter(url); // filters.js에서 정의된 함수 호출
		}
	})
	.catch(error => {
		console.error('Error loading content:', error);
		const contentArea = document.getElementById('dashboard-content');
		if (contentArea) {
			contentArea.innerHTML = `
				<div class="alert alert-danger" role="alert">
					콘텐츠를 로드할 수 없습니다. (${error.message})
				</div>`;
		}
	});
}


