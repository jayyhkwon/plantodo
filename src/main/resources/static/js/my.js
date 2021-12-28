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
function switchTodoDateStatus(todoDateId) {
    let uri = "/todoDate/switching?todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    })
}

function getButtonBlock(planId, todoId) {
    let uri = "/todo/block?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#ButtonBlock').replaceWith(fragment);
    })
}

function getTodoDateRegisterForm(planId) {
    $("#dailyTdRegister" + planId).css("display", "inline");
}

function deleteTodoDate(todoDateId) {
    let form = $('#deleteForm'+todoDateId).serialize();
    $.ajax({
        url: "/todoDate",
        type: "DELETE",
        data: form,
        success: function (res) {
            if (res.pageInfo == "home") {
                loadDateBlockData(res.searchDate);
            } else {
                planDetailAjax(res.planId)
            }
        }
    })
}

function planDetailAjax(planId) {
    $.ajax({
        url: "/plan/" + planId,
        type: "GET"
    }).done( function (fragment) {
        $('body').empty().html(fragment);
    })
}

function registerTodoDateDaily(planId) {
    let form = $('#dailyTdRegisterForm'+planId).serialize();

    $.ajax({
        url: "/todoDate/daily",
        type: "POST",
        data: form,
        success: function(res) {
            let searchDate = res.searchDate;
            loadDateBlockData(searchDate);
        }
    });
}

function getTodoUpdateForm(planId, todoId) {
    let uri = "/todo?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#todoButtonBlock').replaceWith(fragment);
    })
}

function getTodoDateDetailBlock(selectedDate, tododateId) {
    let uri = "/todoDate?todoDateId=" + tododateId + "&selectedDate=" + selectedDate;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#detailBlock'+tododateId).empty().append(fragment);
        $('#comment-register-box'+tododateId).css("display", "inline");
    })
}


$('body').on('click', '#write', function(event) {
    event.preventDefault();
    let todoDateId = $("#todoDateId").val();
    $.ajax({
        url: "/comment",
        type: "POST",
        data: $('#comment-form').serialize()
    }).done(function(fragment) {
        $('#detailBlock'+todoDateId).empty().append(fragment);
        $('#comment').val("");
    })
})

function deleteComment(selectedDate, commentId, todoDateId) {
    let uri = "/comment?selectedDate="+selectedDate+"&commentId="+commentId+"&todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "DELETE"
    }).done(function(fragment) {
        $('#detailBlock'+todoDateId).html(fragment);
    })
}

function getCommentUpdateForm(selectedDate, todoDateId, commentId, comment) {
    let input = document.createElement("input");
    input.id = "commentUpdateInput"
    input.setAttribute("selectedDate", selectedDate);
    input.setAttribute("todoDateId", todoDateId);
    input.setAttribute("commentId", commentId);
    input.value = comment;

    let button = document.createElement("input");
    button.id = "edit"
    button.name = "edit"
    button.type = "button"
    button.value = "edit"

    $('#'+commentId+"title").html(input);
    $('#'+commentId+'editBtn').empty().html(button);
    $('#' + commentId + "buttons").css("display", "none");

}

$('body').on('click', '#edit', function(event) {
    let commentId = $("#commentUpdateInput").attr("commentId");
    let updatedComment = $("#commentUpdateInput").val();
    event.preventDefault();
    $.ajax({
        url: "/comment?commentId="+commentId+"&updatedComment="+updatedComment,
        type: "PUT",
        success: function() {
            let span = document.createElement("span");
            span.id = commentId + "title";
            span.innerText = updatedComment;
            span.value = commentId;

            $("#"+commentId+"title").empty().html(span);
            $("#"+commentId+"editBtn").empty();
            $("#"+commentId+"buttons").css("display", "inline");
        },
        error: function(err) {
            alert(err);
        }
    })
})
