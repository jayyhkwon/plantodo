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
    if ($("#dailyTdRegister"+planId).css("display") === "none") {
        $("#dailyTdRegister" + planId).css("display", "inline");
    } else {
        $("#dailyTdRegister" + planId).css("display", "none");
    }

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


// tododate

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


function getTodoDateEditForm(pageInfo, selectedDate, planId, todoDateId) {
    let div = document.createElement("div");

    let input = document.createElement("input");
    input.id = "editTitle";
    input.name = "editTitle";
    input.setAttribute("pageInfo", pageInfo);
    input.setAttribute("selectedDate", selectedDate);
    input.setAttribute("planId", planId);
    input.setAttribute("todoDateId", todoDateId);

    let btn = document.createElement("input");
    btn.type = "button"
    btn.id = "editBtn"
    btn.name = "editBtn"
    btn.value = "update"
    btn.onclick = function() {
        let pageInfo = $('#editTitle').attr("pageInfo");
        let selectedDate = $('#editTitle').attr("selectedDate");
        let planId = $('#editTitle').attr("planId");
        let todoDateId = $('#editTitle').attr("todoDateId");

        let data = { pageInfo: pageInfo,
            selectedDate: selectedDate,
            planId: planId,
            todoDateId: todoDateId,
            updateTitle: $('#editTitle').val() }

        console.log(data);

        $.ajax({
            url: "/todoDate",
            type: "PUT",
            data: data,
            success: function (res) {
                setTimeout(function () {
                    if (res.pageInfo == "home") {
                        loadDateBlockData(res.searchDate);
                    } else {
                        planDetailAjax(res.planId);
                    }
                }, 100);
            }
        })
    }

    div.appendChild(input);
    div.appendChild(btn);

    $("#" + todoDateId).empty().html(div);
}

// comment

$(document).on("click", "#blockTrigger", function(event) {
    event.preventDefault();
    let selectedDate = $(this).attr("selectedDate");
    let tododateId = $(this).attr("todoDateId");
    let state = $('#detailBlock' + tododateId).data("state");
    if (state === undefined) {
        let uri = "/todoDate?todoDateId=" + tododateId + "&selectedDate=" + selectedDate;
        $.ajax({
            url: uri,
            type: "GET"
        }).done(function(fragment) {
            $('#detailBlock'+tododateId).data("state", "clicked");
            $('#detailBlock'+tododateId).empty().append(fragment);
        })
    } else {
        $('#detailBlock'+tododateId).empty()
        $('#detailBlock'+tododateId).removeData("state");
    }
});


function registerComment(selectedDate, todoDateId) {

    let data = {
        'selectedDate': selectedDate,
        'todoDateId': todoDateId,
        'comment': $('#comment-input').val()
    }

    $.ajax({
        url: "/comment",
        type: "POST",
        data: data
    }).done(function(fragment) {
        $('#detailBlock' + todoDateId).empty().append(fragment);
        $('#comment-input').val("");
    })
}


function deleteComment(selectedDate, commentId, todoDateId) {
    let uri = "/comment?selectedDate="+selectedDate+"&commentId="+commentId+"&todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "DELETE"
    }).done(function(fragment) {
        $('#detailBlock'+todoDateId).html(fragment);
    })
}

function getCommentUpdateForm(selectedDate, todoDateId, commentId) {
    let input = document.createElement("input");
    input.id = "commentUpdateInput"
    input.setAttribute("selectedDate", selectedDate);
    input.setAttribute("todoDateId", todoDateId);
    input.setAttribute("commentId", commentId);
    input.className = "form-control"
    input.value = $('#'+commentId+"title").text();

    $('#'+commentId+"title").html(input);
    $('#' + commentId + "delbtn").css("display", "none");
    $('#' + commentId + "edtBefore").css("display", "none");
    $('#' + commentId + "edtAfter").css("display", "inline");

}

$(document).on('click', '#edit', function(event) {
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
            $("#"+commentId+"delbtn").css("display", "inline");
            $("#"+commentId+"edtbtn").css("display", "inline");
        },
        error: function(err) {
            alert(err);
        }
    })
})
