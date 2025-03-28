import validator from 'validator';
import { msgResponse } from '@utils/functions';

export const checkErrorResponse = (error) => {
  //check if this network part will be needed
  if (error.message === "Network Error") {
    return msgResponse(false, "Problems with server!")
  }
  // check if the errore message is in the response
  if (error.response.data.message) {
    return msgResponse(false, error.response.data.message)
  }
  // check if the error message is from Axios
  if (error.message) {
    return msgResponse(false, error.message)
  }
  return msgResponse(false, "Something went wrong!")
}

export const validateInputs = (value, type) => {
  let errorMsg = ""
  switch (type) {

    //
    // Name
    //
    case 'name':
      if (!/^[a-zA-Z_]+$/.test(value) || value.length === 0) errorMsg = "Name not valid!"
      return errorMsg

    //
    // Email
    //
    case 'email':
      if (value === null || !validator.isEmail(value)) {
        errorMsg = "Email is not valid."
      }
      return errorMsg

    //
    // Password
    //
    case 'password':
      if (value.length < 8) errorMsg = "Password must be at least 8 characters long!"
      else if (!value.match(".*[a-z].*")) errorMsg = "Password must contain at least one lowercase letter!"
      else if (!value.match(".*[A-Z].*")) errorMsg = "Password must contain at least one capital letter!"
      else if (!value.match(".*[0-9].*")) errorMsg = "Password must contain at least one number!"
      return errorMsg

    //
    // Zip Code
    //
    case 'zipCode':
      if (value === null || !/^[0-9]*$/.test(value) || value.length !== 5) {
        errorMsg = "Zip code must be 5 digits."
      }
      return errorMsg

    //
    // Phone Number
    //
    case 'phoneNumber':
      if (value === null || !/^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im.test(value)) {
        errorMsg = "Phone number is not valid."
      }
      return errorMsg

    //
    // Fiscal Code
    //
    case 'fiscalcode':
      value = value.toUpperCase()
      if (value === null || !/^[A-Z]{6}[0-9]{2}[ABCDEHLMPRST]{1}[0-9]{2}([A-Z]{1}[0-9]{3})[A-Z]{1}$/.test(value)) {
        errorMsg = "Fiscal code formally incorrect"
      }
      return errorMsg

    //
    // Default
    //
    default:
      return errorMsg
  }
}

export const checkFieldLength = (s, min, max) => {
  let errorMsg = ""
  if (s === null || s.length < min || s.length > max) {
    errorMsg = `Field length must be between ${min} and ${max} characters.`
  }
  return errorMsg
}