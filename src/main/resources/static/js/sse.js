import * as commonService from "./common.js";

/*Main Logic*/
let dat = commonService.getCookieVal("deadline_alarm_term");
if (dat !== null && dat !== "-1") {
    eventSourceLogic();
}

function eventSourceLogic() {
    /*subscribe*/
    fetch(`/sse/able`).then(
        response => response.text()
    ).then(
        result => {
            console.log(JSON.parse(result).able);
            if (JSON.parse(result).able === true) {
                let uri = "/sse/subscribe";
                var es = new EventSource(uri);

                fetch(`/sse/sendAlarm`);

                /*sendAlarm*/
                es.onopen = (e) => {
                    console.log(e);
                }

                es.onerror = (e) => {
                    if (e.currentTarget.readyState === EventSource.CLOSED) {
                        new Notification('푸시 알림 전송이 중단되었습니다. 푸시 알림을 이어서 받고 싶다면 페이지를 새로고침해 주세요.');
                    } else if (e.currentTarget.readyState === EventSource.CONNECTING) {
                        new Notification('서버가 다운되었습니다. 이후에 다시 사이트에 접속해 주세요.');
                        es.close();
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
        }
    );
}