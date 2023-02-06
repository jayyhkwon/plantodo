import * as modalService from './permission_modal.js';
import * as commonService from './common.js';


function joinRequestPermission() {
    if (Notification.permission === "default") {
        modalService.showPermModal();
    } else {
        joinProcess((Notification.permission === "denied" ? "MISTOOK" : "GRANTED"));
    }
}

function joinProcess(permission) {
    $.ajax({
        uri: "/member/join",
        type: "POST",
        data: {
            email: $("#email").val(),
            password: $("#password").val(),
            nickname: $("#nickname").val(),
            permission: permission
        },
        success: function(res) {
            $('body').empty().html(res);
        }
    });
}

$(document).ready(function() {
    let btn = document.getElementById("join_submit");
    btn.addEventListener('click', joinRequestPermission);

    let grantedBtn = document.getElementById("grantedBtn");
    grantedBtn.addEventListener('click', function() {
        Notification.requestPermission().then(function(permission) {
            joinProcess(commonService.parsePermission(permission));
        })
    });

    let deniedBtn = document.getElementById("deniedBtn");
    deniedBtn.addEventListener('click', function() {
        joinProcess("denied");
    });
})

export {joinRequestPermission, joinProcess};