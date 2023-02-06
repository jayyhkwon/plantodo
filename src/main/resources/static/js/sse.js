import * as commonService from "./common.js";

console.log("deadline_alarm_term : " + commonService.getCookieVal("deadline_alarm_term"));
console.log("firstAccess : " + commonService.getCookieVal("firstAccess"));

/*Main Logic*/
if (commonService.getCookieVal("deadline_alarm_term") !== "-1") {
    if (commonService.getCookieVal("firstAccess") === "1") {
        initMsgLogic();
    } else {
        succeededMsgLogic();
    }
}

/*세부 로직 함수들*/
function getWaitTime(lst, interval) {
    lst = Number(lst);
    interval *= 60000;
    while (lst <= new Date().getTime()) {
        lst += interval;
    }
    return lst - new Date().getTime();
}

function initMsgLogic() {

    /*subscribe*/
    let uri = "/sse/subscribe";
    const eventSource_start = new EventSource(uri);

    /*sendAlarm*/
    fetch(`/sse/sendAlarm`);

    eventSource_start.onopen = (e) => {
        console.log(e);
    }

    eventSource_start.onerror = (e) => {
        if (e.currentTarget.readyState == EventSource.CLOSED) {
        } else {
            eventSource.close();
        }
    }

    eventSource_start.onmessage = (e) => {

        /*첫 로그인 쿠키 삭제*/
        commonService.deleteCookie("firstAccess");

        let deadline_alarm_term = Number(commonService.getCookieVal("deadline_alarm_term"));

        let data = JSON.parse(e.data);
        let msg = "아직 완료되지 않은 일정이 " + data.count + "개 있습니다. 가장 마감이 임박한 일정으로 이동하시겠습니까?";
        let notification = new Notification('현재 메세지', {title: "마감 알림", body: msg});
        notification.onclick = function (e) {
            e.preventDefault();
            window.open("/plan/" + data.planId, '_blank');
        }
        setTimeout(notification.close.bind(notification), deadline_alarm_term * 60000);
    }
}

function succeededMsgLogic() {
    $.ajax({
        uri: "/sse/last",
        type: "GET"
    }).done(function(response) {
        console.log(response);
        let msgLastSentTime = response;
        if (msgLastSentTime != null) {
            let deadline_alarm_term = commonService.getCookieVal("deadline_alarm_term");
            let waitTime = getWaitTime(msgLastSentTime, deadline_alarm_term);

            /*waitTime만큼 기다리기*/
            setTimeout(function () {
                /*subscribe*/
                let uri = "/sse/subscribe";
                const eventSource_dur = new EventSource(uri);

                /*sendAlarm*/
                fetch(`/sse/sendAlarm`);

                /*eventSource onopen, onerror, onmessage*/
                eventSource_dur.onopen = (e) => {
                    console.log(e);
                }

                eventSource_dur.onerror = (e) => {
                    if (e.currentTarget.readyState == EventSource.CLOSED) {
                    } else {
                        eventSource.close();
                    }
                }

                eventSource_dur.onmessage = (e) => {
                    let data = JSON.parse(e.data);
                    let msg = "아직 완료되지 않은 일정이 " + data.count + "개 있습니다. 가장 마감이 임박한 일정으로 이동하시겠습니까?";
                    let notification = new Notification('현재 메세지', {title: "마감 알림", body: msg});
                    notification.onclick = function (e) {
                        e.preventDefault();
                        window.open("/plan/" + data.planId, '_blank');
                    }
                    let now = new Date().getTime();
                    setTimeout(notification.close.bind(notification), deadline_alarm_term * 60000);
                    commonService.setCookie("msgLastSentTime", now);
                }
            }, waitTime);
        } else {
            initMsgLogic();
        }

    })


}