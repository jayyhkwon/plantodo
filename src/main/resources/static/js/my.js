

/*일자별 Plan, To-do 조회*/
function loadDateBlockData(searchDate, days) {
    $('a[id^="eachDate"]').css("color", "black");
    $('#eachDate'+days).css("color", "blue");
    let uri = "/home/calendar/" + searchDate;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function (fragment) {
        $("#dateBlock").replaceWith(fragment);
    });
}

/*plan*/
function selectType() {
    if ($('#selectType').css('display') === 'none') {
        $('#planTypeBtn').removeClass();
        $('#planTypeBtn').addClass('btn btn-primary');
        $('#selectType').show();
    } else {
        $('#planTypeBtn').removeClass();
        $('#planTypeBtn').addClass('btn btn-outline-primary');
        $('#selectType').hide();
    }
}

$('#prNoTermModal').on('show.bs.modal', function() {
    $.ajax({
        url: '/plan/regular',
        method: 'GET',
        success: function (res) {
            $('#prNoTermModal-body').html(res);
        }
    })
})

$('#prWithTermModal').on('show.bs.modal', function() {
    $.ajax({
        url: '/plan/term',
        method: 'GET',
        success: function (res) {
            $('#prWithTernModal-body').html(res);
        }
    })
})

$('#tdrModal').on('show.bs.modal', function() {
    $.ajax({
        url: '/todo/register',
        method: 'GET',
        success: function (res) {
            $('#tdrModal-body').html(res);
        }
    })
})

$('[close]').on('click', function() {
    location.reload();
})

$('#tdrModal').on('click', '#tdr-submit', function() {
    let form = $('#tdr-form').serialize();
    $.ajax({
        url: '/todo/register',
        method: 'POST',
        data: form,
        success: function(res) {
            let tempElement = $('<html></html>').html(res);
            let pageType = tempElement.find('input[id="pageType"]').val();

            if (pageType === 'home') {
                $('#tdrModalClose').click();
                location.reload();
            } else {
                $('#tdrModal-body').html(res);
            }
        }
    })
})


function getPlanDetailPage(planId) {
    console.log(planId);
    $('#plan-detail-trigger'+planId)[0].click();
}

function planDetailAjax(planId) {
    $.ajax({
        url: "/plan/" + planId,
        type: "GET"
    }).done( function (fragment) {
        $('body').empty().html(fragment);
    })
}

function switchPlanEmphasis(planId, pageInfo) {
    let uri = "/plan/emphasizing?planId="+planId+"&pageInfo="+pageInfo ;
    $.ajax({
        url: uri,
        type: "POST"
    })
}


/*to-do*/
function getTodoEditForm(planId, todoId) {
    $.ajax({
        url: '/todo/todo?planId='+planId+"&todoId="+todoId,
        method: 'GET',
        success: function (res) {
            $('#tdEditModal-body').html(res);
            $('#tdEditModalTodoId').val(todoId);
            $('#tdEditModelPlanId').val(planId);
            $('#tdEditModal-body').show();
            let tdEditModal = new bootstrap.Modal(document.getElementById('tdEditModal'));
            tdEditModal.show();
        }
    })
}

document.addEventListener('DOMContentLoaded', function(event) {
    document.addEventListener('change', function(event) {
        if (event.target.id === 'repOption-register') {
            let option = event.target.options[event.target.selectedIndex];
            $.ajax({
                url: '/todo/emptyRepOptForm?newRepOpt='+option.value,
                method: 'GET',
                success: function(res) {
                    $('#repOptionBody').html(res);
                }
            })
        }
    })
})

document.addEventListener('DOMContentLoaded', function(event) {

    document.addEventListener('change', function(event) {
        if (event.target.id === 'repOption-update') {
            let todoId = $('#tdEditModalTodoId').val();
            let planId = $('#tdEditModelPlanId').val();
            let option = event.target.options[event.target.selectedIndex];
            $.ajax({
                url: '/todo/repOptForm?planId=' + planId + '&todoId=' + todoId + '&newRepOpt='+option.value,
                method: 'GET',
                success: function(res) {
                    $('#repOptionBody').html(res);
                }
            })
        }
    });

})

function deleteTodo(planId, todoId) {
    $.ajax({
        url: '/todo?planId='+planId+"&todoId="+todoId,
        method: 'DELETE',
        success: function() {
            location.reload();
        }
    })
}

/*todoDate*/
function switchTodoDateStatus_home(todoDateId, planId) {
    let uri = "/todoDate/switching?todoDateId="+todoDateId + "&planId="+planId;
    $.ajax({
        url: uri,
        type: "POST"
    })
}

function switchTodoDateStatus_detail(todoDateId, planId) {
    let uri = "/todoDate/switching?todoDateId="+todoDateId + "&planId="+planId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function (data) {
        console.log(data);
        $("#progress_bar").css("width", data+"%");
        $("#progress_bar").data("aria-valuenow", data);
        $("#progress_bar").text(data + "%");
    })

}

function getTodoButtonBlock(planId, todoId) {
    let uri = "/todo/block?planId=" + planId + "&todoId=" + todoId;

    let state = $('#' + todoId + 'ButtonBlock').data('state');
    if (state === undefined) {
        $.ajax({
            url: uri,
            type: "GET"
        }).done(function(fragment) {
            $('#' + todoId + 'ButtonBlock').data("state", "clicked");
            $('#'+todoId+'ButtonBlock').empty().append(fragment);
        })
    } else {
        $('#' + todoId + 'ButtonBlock').empty();
        $('#' + todoId + 'ButtonBlock').removeData("state");
    }

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
    // text와 edit, del 버튼 가리고 form 보여주기
    let tdd_content = $('#todoDate-content'+todoDateId);
    let tdd_edit_box = $('#todoDate-edit-box'+todoDateId);
    tdd_content.hide();
    tdd_edit_box.val(tdd_content.text()).show().focus();

    $('#todoDate-btn-g1'+todoDateId).hide();
    $('#todoDate-btn-g2'+todoDateId).show();
}

function afterTodoDateEdit(pageInfo, selectedDate, planId, todoDateId) {

    let updatedTitle = $('#todoDate-edit-box'+todoDateId).val();
    let data = { pageInfo: pageInfo,
        selectedDate: selectedDate,
        planId: planId,
        todoDateId: todoDateId,
        updateTitle: updatedTitle }

    $.ajax({
        url: "/todoDate",
        type: "PUT",
        data: data,
        success: function (res) {

            $('#todoDate-content'+todoDateId).val(updatedTitle).text(updatedTitle).css("display", "inline");
            $('#todoDate-edit-box'+todoDateId).css("display", "none");

            $('#todoDate-btn-g2'+todoDateId).css("display", "none");
            $('#todoDate-btn-g1'+todoDateId).css("display", "inline");
        }
    })
}

function todoDateEditBack(todoDateId) {
    $('#todoDate-content'+todoDateId).css("display", "inline");
    $('#todoDate-edit-box'+todoDateId).css("display", "none");

    $('#todoDate-btn-g2'+todoDateId).css("display", "none");
    $('#todoDate-btn-g1'+todoDateId).css("display", "inline");
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


function editComment() {
    let commentId = $("#commentUpdateInput").attr("commentId");
    let updatedComment = $("#commentUpdateInput").val();
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
}


