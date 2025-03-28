import { NAME } from "@constants"

export const msgResponse = (success = true, msg = "Success") => {
    return {
        isSuccess: success,
        message: msg
    }
}

export const msgLogout = (success = false, msg = "Logout", logout = true) => {
    return {
        isSuccess: success,
        message: msg,
        logout: logout
    }
}

export const correctDate = (date) => {
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
}

export const formatIsoDate = (isoDate) => {
    const date = new Date(isoDate)
    const day = String(date.getDate()).padStart(2, '0')
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const year = date.getFullYear()
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${day}/${month}/${year} ${hours}:${minutes}`
}

export const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = date.getHours()
    const minutes = date.getMinutes()

    // Check if the input string contains time information
    const hasTime = dateString.includes('T') || dateString.includes(' ')
    if (!hasTime) {
        return `${year}-${month}-${day}`
    }

    const formattedHours = String(hours).padStart(2, '0')
    const formattedMinutes = String(minutes).padStart(2, '0')
    return `${year}-${month}-${day}T${formattedHours}:${formattedMinutes}`
}

export const daysSinceDate = (isoDate) => {
    const date = new Date(isoDate)
    const today = new Date()
    const differenceInTime = today - date
    const differenceInDays = Math.floor(differenceInTime / (1000 * 60 * 60 * 24))
    return differenceInDays
}

export const getRandomNumber = () => {
    return Math.floor(Math.random() * 1000) + 1
}

export const getUserFullName = () => {
    const fullname = localStorage.getItem(NAME) || sessionStorage.getItem(NAME)
        ? localStorage.getItem(NAME) || sessionStorage.getItem(NAME)
        : 'N/A'
    return fullname

}
