
function showPermModal() {
    var myModal = new bootstrap.Modal(document.getElementById('myModal'), {
        keyboard: false
    })
    myModal.show();
}

function hidePermModal() {
    var myModal = new bootstrap.Modal(document.getElementById('myModal'), {
        keyboard: false
    })
    myModal.hide();
}

export {showPermModal, hidePermModal};