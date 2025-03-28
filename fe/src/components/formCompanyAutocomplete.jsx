import React, { useState, useEffect, useContext } from 'react'
import {
  Col,
  Form,
  FloatingLabel,
  ListGroup,
  Row,
  Spinner,
} from 'react-bootstrap'
import { getData } from '@utils/api'
import { SERVER_COMPANIES } from '@endpoints'
import { ProfileContext } from '@pages/profile'

const FormCompanyAutocomplete = ({
  setSelectedCompany,
  fieldName,
  placeholder,
  required,
  disabled,
}) => {
  const { userData, setUserData } = useContext(ProfileContext)

  const [formData, setFormData] = useState({
    autocompleteCompanyDescription:
      userData.jobProfile.company.description || '',
  })
  const [isLoading, setIsLoading] = useState(false)
  const [suggestions, setSuggestions] = useState([])
  const [isFocused, setIsFocused] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')

  const handleInputChange = (e) => {
    // if there is an error and then the user starts typing again, clear the error message
    if (errorMsg.length > 0) setErrorMsg('')

    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const fetchCompanySuggestions = async (query) => {
    // if the field is disabled, do not fetch the data
    if (disabled) return []

    setIsLoading(true)
    try {
      const params = {
        page: 0,
        size: 100,
        sortBy: 'description',
        direction: 'ASC',
        q: query,
      }

      const data = await getData(SERVER_COMPANIES, params)

      if (data.logout) {
        navigate(LOGOUT)
      } else if (data.isSuccess) {
        return data._embedded.companyList || []
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrorMsg('Company not found')
      } else {
        setErrorMsg('Server fetching error: ' + error.message)
      }
      return []
    } finally {
      setIsLoading(false)
    }
  }

  const handleSuggestionClick = (suggestion) => {
    setFormData({ autocompleteCompanyDescription: suggestion.description })
    // Set the father component state
    setSelectedCompany(fieldName, suggestion)
    setSuggestions([])
  }

  //
  // It will fetch the company suggestions when the user types in the input field
  // The useEffect monitors the changes in the formData.autocompleteCompanyDescription
  //
  useEffect(() => {
    // if the field is empty, clear the suggestions and do not fetch any data
    if (!formData.autocompleteCompanyDescription.trim()) {
      setSuggestions([])
      return
    }

    // wait 300ms before fetching the data
    const debounceTimer = setTimeout(async () => {
      const companies = await fetchCompanySuggestions(
        formData.autocompleteCompanyDescription
      )
      setSuggestions(companies)
    }, 300)

    // clear the timer if the user types again
    // this way the fetch will be triggered only when the user stops typing
    return () => clearTimeout(debounceTimer)
  }, [formData.autocompleteCompanyDescription])

  //
  // It will set the company description when the userData changes
  // This is needed when the user is editing the profile and the company description is already set
  //
  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      autocompleteCompanyDescription: userData.jobProfile.company.description,
    }))
  }, [userData])

  return (
    <div className='position-relative'>
      <FloatingLabel label={placeholder} className='mb-3'>
        <Form.Control
          type='text'
          placeholder={placeholder}
          name='autocompleteCompanyDescription'
          value={formData.autocompleteCompanyDescription}
          onChange={handleInputChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setTimeout(() => setIsFocused(false), 200)}
          required={required}
          disabled={disabled}
        />
      </FloatingLabel>

      {isLoading && (
        <Spinner
          animation='border'
          size='sm'
          className='position-absolute'
          style={{ top: '18px', right: '15px' }}
        />
      )}

      {errorMsg && (
        <div
          className='text-danger position-absolute'
          style={{ fontSize: '.8em', top: '18px', right: '15px' }}
        >
          {errorMsg}
        </div>
      )}

      {isFocused && suggestions.length > 0 && (
        <ListGroup
          className='shadow'
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            zIndex: 1000,
            maxHeight: '200px',
            overflowY: 'auto',
          }}
        >
          {suggestions.map((suggestion) => (
            <ListGroup.Item
              key={suggestion.id}
              action
              onMouseDown={() => handleSuggestionClick(suggestion)}
            >
              {suggestion.description} - ({suggestion.city || ''})
            </ListGroup.Item>
          ))}
        </ListGroup>
      )}
    </div>
  )
}

export default FormCompanyAutocomplete
