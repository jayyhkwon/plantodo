import * as commonService from "./common.js";

/*Main Logic*/
let dat = commonService.getCookieVal("deadline_alarm_term");
if (dat !== null && dat !== "-1") {
    eventSourceLogic();
}

function eventSourceLogic() {
    /*subscribe*/
    let uri = "/sse/subscribe";
    var es = new EventSource(uri);

    /*sendAlarm*/
    fetch(`/sse/sendAlarm`);

    es.onopen = (e) => {
        console.log(e);
    }

    es.onerror = (e) => {
        if (e.currentTarget.readyState === EventSource.CLOSED) {
        } else if (e.currentTarget.readyState === EventSource.CONNECTING) {
            setTimeout(function() {
                es = new EventSource(uri);
            }, 1000);
        } else {
            console.log(e);
        }
    }

    es.onmessage = (e) => {
        let data = JSON.parse(e.data);
        let msg = "아직 완료되지 않은 일정이 " + data.count + "개 있습니다. 가장 마감이 임박한 일정으로 이동하시겠습니까?";
        let notification = new Notification('현재 메세지', {title: "마감 알림", body: msg});
        notification.onclick = function (e) {
            e.preventDefault();
            window.open("/plan/" + data.planId, '_blank');
        }
    }
}