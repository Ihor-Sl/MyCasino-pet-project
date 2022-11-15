function ban(id) {
    fetch(`/admin/ban/${id}`)
        .then(data => {
            document.getElementById(id).outerHTML = `<button id=\"${id}\" onclick=\"unBan(${id})\"  class=\"btn btn-success m-auto mx-0\" style=\"width: 200px; height: 50px\">Unban</button>`
        })
}

function unBan(id) {
    fetch(`/admin/unBan/${id}`)
        .then(data => {
            document.getElementById(id).outerHTML = `<button id=\"${id}\" onclick=\"ban(${id})\"  class=\"btn btn-danger m-auto mx-0\" style=\"width: 200px; height: 50px\">Ban</button>`
        })
}