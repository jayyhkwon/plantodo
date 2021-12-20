/*일자별 Plan, To-do 조회*/
function loadDateBlockData(searchDate) {
    let uri = "/home/calendar/" + searchDate;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function (fragment) {
        $("#dateBlock").replaceWith(fragment);
    });
}

/*to-do 상태 바꾸기*/
function planSwitchTodoDateStatus(planId, todoDateId) {
    let uri = "/plan/todoDate/switching?planId=" + planId + "&todoDateId=" +todoDateId;
    let tagId = "#" + todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function (fragment) {
        $('#body').replaceWith(fragment);
    });
}

function homeSwtichTodoDateStatus(selectedDate, todoDateId) {
    let uri = "/home/calendar/todoDate/switching?selectedDate=" + selectedDate + "&todoDateId=" + todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function(fragment) {
        $('#dateBlock').replaceWith(fragment);
    })
}

function getButtonBlock(planId, todoId) {
    let uri = "/plan/todo/block?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#ButtonBlock').replaceWith(fragment);
    })
}

function getTodoUpdateForm(planId, todoId) {
    let uri = "/plan/todo?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#todoButtonBlock').replaceWith(fragment);
    })
}