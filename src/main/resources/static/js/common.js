
function isCookieExists(name) {
    name = name + "=";
    let cookies = document.cookie;
    let start = cookies.indexOf(name);
    return false ? start == -1 : true;
}

function getCookieVal(name) {
    let cookies = document.cookie.split(";");
    let result = ""
    for (let i=0; i<cookies.length; i++) {
        if (cookies[i][0] === " ") {
            cookies[i] = cookies[i].substring(1);
        }
        if (cookies[i].indexOf(name) === 0) {
            result = cookies[i].slice(name.length+1, cookies[i].length);
            return result;
        }
    }
    return result;
}

function deleteCookie(name) {
    document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
}

function setCookie(cName, cValue) {
    document.cookie = cName + "=" + cValue + "; SameSite=None; Secure; Path=/";
}

function parsePermission(word) {
    return (word === 'denied' ? 'mistook' : word);
}

export {isCookieExists, getCookieVal, deleteCookie, setCookie, parsePermission};