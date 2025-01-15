$(function() {
  // 전화번호 형식 자동 변환
  $("#phone").on("input", function() {
      let number = $(this).val().replace(/[^0-9]/g, "").substr(0, 11);
      if (number.length > 3) {
          number = number.length > 7 
              ? `${number.substr(0,3)}-${number.substr(3,4)}-${number.substr(7)}`
              : `${number.substr(0,3)}-${number.substr(3)}`;
      }
      $(this).val(number);
  });

  // 아이디 마스킹 처리 함수
  function maskMemberId(memberId) {
    if (!memberId) return "";
    const length = memberId.length;
    let visibleLength = Math.min(4, Math.ceil(length / 2));
    return memberId.substring(0, visibleLength) + "*".repeat(length - visibleLength);
}

  // 페이지 로드 시 마스킹 처리
  if ($("#resultModal").is(":visible")) {
      const idElement = $("#maskedId");
      const originalId = idElement.attr("data-id");
      if (originalId) {
          const maskedId = maskMemberId(originalId);
          idElement.text(maskedId);
      }
  }

  // 모달 닫기 버튼 이벤트
  $(".btn-close, .modal-backdrop, [data-bs-dismiss='modal']").on("click", function() {
      $("#resultModal").hide();
      $(".modal-backdrop").remove();
  });
}); 