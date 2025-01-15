$(document).ready(function () {
    // 오늘 날짜를 YYYY-MM-DD 형식으로 변환하는 함수
    function formatDate(date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('-');
    }

    var today = new Date();
    $('#startPicker').val(formatDate(today));
    $('#endPicker').val(formatDate(today));

    // 한국어로 된 날짜 선택기를 위한 언어 설정
    $.fn.datepicker.dates['ko'] = {
        days: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
        daysShort: ["일", "월", "화", "수", "목", "금", "토"],
        daysMin: ["일", "월", "화", "수", "목", "금", "토"],
        months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        today: "오늘",
        clear: "삭제",
        format: "yyyy-mm-dd",
        titleFormat: "yyyy년mm월",
        weekStart: 0
    };

    // 시작 날짜 선택기 설정
    $('#startPicker').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        language: 'ko',
        todayHighlight: true,
        orientation: "bottom auto",
        endDate: today
    }).on('changeDate', function (selected) {
        var startDate = new Date(selected.date.valueOf());
        var maxDate = new Date(startDate);
        maxDate.setFullYear(maxDate.getFullYear() + 1); // 시작일로부터 1년 후

        // 최대 날짜를 오늘과 시작일+1년 중 더 이른 날짜로 설정
        var endLimit = maxDate < today ? maxDate : today;

        $('#endPicker').datepicker('setStartDate', startDate); // 시작일 이상만 선택 가능
        $('#endPicker').datepicker('setEndDate', endLimit);    // 1년 후와 오늘 중 더 이른 날짜까지만 선택 가능

        // 현재 선택된 종료일이 새로운 범위를 벗어나면 시작일로 리셋
        var currentEndDate = $('#endPicker').datepicker('getDate');
        if (currentEndDate < startDate || currentEndDate > endLimit) {
            $('#endPicker').datepicker('setDate', startDate);
        }
    });

    // 종료 날짜 선택기 설정
    $('#endPicker').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        language: 'ko',
        todayHighlight: true,
        orientation: "bottom auto",
        startDate: today, // 초기화 시에는 오늘 날짜 이상만 선택 가능
        endDate: today
    });
});
