document.addEventListener('DOMContentLoaded', function() {
  const leftMenu = document.querySelector('.left-menu');
  const submenuContainer = document.querySelector('.submenu-container');
  const chatModal = document.getElementById('chatModal');
  const notificationModal = document.getElementById('notificationModal');
  const chatIcon = document.querySelector('.right-menu .nav-link:first-child');
  const notificationIcon = document.querySelector('.right-menu .nav-link:last-child');

  // 왼쪽 메뉴 호버 시 서브메뉴 표시
  leftMenu.addEventListener('mouseenter', function() {
    submenuContainer.classList.add('show');
  });

  leftMenu.addEventListener('mouseleave', function(e) {
    if (!e.relatedTarget || !e.relatedTarget.closest('.submenu-container')) {
      submenuContainer.classList.remove('show');
    }
  });

  submenuContainer.addEventListener('mouseenter', function() {
    submenuContainer.classList.add('show');
  });

  submenuContainer.addEventListener('mouseleave', function() {
    submenuContainer.classList.remove('show');
  });

  // 채팅 모달 관련 코드 수정
  chatIcon.addEventListener('click', function(e) {
    e.preventDefault();
    const chatModalInstance = new bootstrap.Modal(chatModal, { backdrop: false });
    chatModalInstance.show();
  });

  // 알림 모달 관련 코드 추가
  notificationIcon.addEventListener('click', function(e) {
    e.preventDefault();
    const notificationModalInstance = new bootstrap.Modal(notificationModal, { backdrop: false });
    notificationModalInstance.show();
  });

  // 모달 위치 조정
  const adjustModalPosition = (modalElement, iconElement) => {
    modalElement.addEventListener('show.bs.modal', function () {
      const modalDialog = this.querySelector('.modal-dialog');
      const rect = iconElement.getBoundingClientRect();
      const iconAbsoluteCenter = rect.left + (rect.width / 2);
      const modalWidth = 350;

      modalDialog.style.position = 'fixed';
      modalDialog.style.width = `${modalWidth}px`;
      modalDialog.style.left = `${iconAbsoluteCenter - (modalWidth / 2)}px`;
      modalDialog.style.margin = '0';
      modalDialog.style.transform = 'none';
    });
  };

  adjustModalPosition(chatModal, chatIcon);
  adjustModalPosition(notificationModal, notificationIcon);

  // 모달창 외부 클릭 시 닫기 기능 추가
  document.addEventListener('click', function(e) {
    const closeModalIfOutsideClick = (modalElement, iconElement) => {
      if (modalElement.classList.contains('show') && 
          !e.target.closest('.modal-dialog') && 
          !iconElement.contains(e.target)) {
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        modalInstance.hide();
      }
    };

    closeModalIfOutsideClick(chatModal, chatIcon);
    closeModalIfOutsideClick(notificationModal, notificationIcon);
  });
});
