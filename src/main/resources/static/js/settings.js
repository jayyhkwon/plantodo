import * as modalService from "./permission_modal.js"
import * as commonService from "./common.js";

/*settings*/
/*termBlock style을 none <- -> inline으로 변경한다.*/
$(document).ready(function() {
    $("#deadline_alarm").click(function() {
        if ($("#termBlock").css("display") === "none") {
            if (Notification.permission === "denied") {
                $("#perm_warning").css("display", "inline");
                $("#deadline_alarm").prop('checked', false);
            } else {
                $("#termBlock").css("display", "inline");
            }
        } else {
            /*on -> off인 경우 quitAlarm한다.*/
            fetch("/sse/quitAlarm");

            /*deadline_alarm을 false로 만들고 deadline_alarm_term을 0으로 초기화한다.*/
            fetch("/settings/update", {
                method: 'post',
                body: JSON.stringify({
                    'settings_id': $('#settings_id').val(),
                    'notification_perm': commonService.parsePermission(Notification.permission).toUpperCase,
                    'deadline_alarm': false,
                    'deadline_alarm_term': 0
                }),
                headers: {"Content-Type": "application/json"}
            }).then(function() {
                $("#alarm_term_text").text("");
                $("#termBlock").css("display", "none");
                $("#deadline_alarm_term").val(0);
            })
        }
    })
});


/*submit 버튼 클릭 시*/
$(document).ready(function () {
    $("#deadline_alarm_submit").click(function () {
        /*현재 진행중인 sse연결을 끊는다. (간격이 바뀌었을 수 있기 때문)*/
        fetch("/sse/quitAlarm");

        /*이미 notification perm을 얻은 상태인지 아닌지 확인하고 얻지 않은 경우 모달 창을 띄운다.*/
        if (Notification.permission === "default") {
            modalService.showPermModal();
        } else {
            afterGrantedConfirmed();
        }
    })
})

/*모달 창에서 granted를 선택한 경우*/
$(document).on("click", "#grantedBtn", function(event) {
    modalService.hidePermModal();
    Notification.requestPermission().then(function(permission) {
        if (permission === 'granted') {
            afterGrantedConfirmed();
        } else {
            afterDeniedConfirmed();
        }
    });

})

/*모달 창에서 denied를 선택한 경우 모달 창을 닫고 경고 창을 띄운다. Notification.permission의 변화 없음*/
$(document).on("click", "#deniedBtn", function(event) {
    afterDeniedConfirmed();
})

function afterDeniedConfirmed() {
    modalService.hidePermModal();
    $("#perm_warning").css("display", "inline");
}

function afterGrantedConfirmed() {
    let data = JSON.stringify({
        settings_id: $('#settings_id').val(),
        notification_perm: "GRANTED",
        deadline_alarm: true,
        deadline_alarm_term: $('#deadline_alarm_term').val()
    });

    /*폼 정보를 update*/
    fetch("/settings/update", {
        method: "POST",
        body: data,
        headers: {"Content-Type": "application/json"}
    });

    $("#deadline_alarm_label").text("마감 알림 : " + $('#deadline_alarm_term').val() + "분마다")
    commonService.setCookie("deadline_alarm_term", $('#deadline_alarm_term').val());
    commonService.setCookie("firstAccess", "1");
}

export const name = "settings";