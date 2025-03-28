import { JWT, TYPE, NAME } from '@constants';
import { jwtDecode as jwt_decode } from 'jwt-decode';

export const isAuthenticated = () => {
    return localStorage.getItem(JWT) !== null || sessionStorage.getItem(JWT) !== null
}

export const saveUser = (token, name, me) => {
    const type = localStorage.getItem(TYPE)
    // First it removes the previous token and name, if any
    logout()
    if (type === null || type === "local") {
        localStorage.setItem(JWT, token)
        localStorage.setItem(NAME, name)
        localStorage.setItem('me', JSON.stringify(me))
    }
    else {
        sessionStorage.setItem(JWT, token)
        sessionStorage.setItem(NAME, name)
        sessionStorage.setItem('me', JSON.stringify(me))
    }
}

export const configure = (type = 'application/json') => {
    //console.log("Configuring")
    if (!isAuthenticated()) {
        //console.log("User is not authenticated")
        return {
            Accept: type
        }
    }
    //console.log("User is authenticated")
    return {
        Accept: type,
        Authorization: `Bearer ${decideTokenValue()}`
    }
}

export const decideTokenValue = () => {
    return localStorage.getItem(JWT) ? localStorage.getItem(JWT) : sessionStorage.getItem(JWT)
}

export const tokenCheck = (error, setLogout) => {
    if (error.response.data.trace.includes("ExpiredJwtException")) {
        setLogout(true)
        return true
    }
}

export const getUsername = () => {
    return localStorage.getItem(JWT) ? jwt_decode(localStorage.getItem(JWT)).sub : jwt_decode(sessionStorage.getItem(JWT)).sub
}

export const getMe = () => {
    const me = localStorage.getItem('me') ? JSON.parse(localStorage.getItem('me')) : JSON.parse(sessionStorage.getItem('me'))
    return me;
}

export const getUserId = () => {
    const me = getMe()
    return me.userId
}

export const getDocTemplatesAllowed = () => {
    const me = getMe()
    return me.docTemplatesAllowed || []
}

export const isAdmin = () => {
    return isRole("admin");
}

export const isRole = (role) => {
    const roles = localStorage.getItem(JWT) ?
        jwt_decode(localStorage.getItem(JWT)).roles :
        jwt_decode(sessionStorage.getItem(JWT)).roles

    if (!roles || roles.length === 0) {
        return false;
    }

    for (let i = 0; i < roles.length; i++) {
        if (roles[i].toLowerCase() === role.toLowerCase() || roles[i].toLowerCase() === "role_" + role.toLowerCase()) {
            return true;
        }
    }

    return false;
}

export const isTokenExpiered = () => {
    try {
        return localStorage.getItem(JWT) ?
            Date.now() > jwt_decode(localStorage.getItem(JWT)).exp * 1000 :
            Date.now() > jwt_decode(sessionStorage.getItem(JWT)).exp * 1000
    }
    catch (e) {
        return true
    }
}

export const checkToken = () => {
    if (isAuthenticated() && isTokenExpiered()) {
        return true
    }
    return false
}

export const logout = () => {
    localStorage.clear();
    sessionStorage.clear();
}
