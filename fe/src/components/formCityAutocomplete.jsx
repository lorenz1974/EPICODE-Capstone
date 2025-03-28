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
import { SERVER_CITIES } from '@endpoints'
import { ProfileContext } from '@pages/profile'

const FormCityAutocomplete = ({
  setCitySelected,
  fieldName,
  placeholder,
  initialValue,
  required,
  disabled,
}) => {
  const { userData, setUserData } = useContext(ProfileContext)

  const [formData, setFormData] = useState({
    autocompleteCityName: initialValue,
  })
  const [isLoading, setIsLoading] = useState(false)
  const [suggestions, setSuggestions] = useState([])
  const [isFocused, setIsFocused] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')

  const handleInputChange = (e) => {
    // if there is an error and then the user starts typing again, clear the error message
    setErrorMsg('')

    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const fetchCitySuggestions = async (query) => {
    // if the field is disabled, do not fetch the data
    if (disabled) return []

    setIsLoading(true)
    try {
      const params = {
        page: 0,
        size: 100,
        sortBy: 'cityState',
        direction: 'ASC',
        q: query,
      }

      const data = await getData(SERVER_CITIES, params)

      if (data.logout) {
        navigate(LOGOUT)
      } else if (data.isSuccess) {
        return data._embedded.cityList || []
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrorMsg('City not found')
      } else {
        setErrorMsg('Server fetching error: ' + error.message)
      }
      return []
    } finally {
      setIsLoading(false)
    }
  }

  const handleSuggestionClick = (suggestion) => {
    setFormData({ autocompleteCityName: suggestion.cityState })
    // Set the father component state
    setCitySelected(fieldName, suggestion)
    setSuggestions([])
  }

  useEffect(() => {
    if (!formData.autocompleteCityName.trim()) {
      setSuggestions([])
      return
    }

    const debounceTimer = setTimeout(async () => {
      const cities = await fetchCitySuggestions(formData.autocompleteCityName)
      setSuggestions(cities)
    }, 300)

    return () => clearTimeout(debounceTimer)
  }, [formData.autocompleteCityName])

  //
  // It will set the company description when the userData changes
  // This is needed when the user is editing the profile and the company description is already set
  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      autocompleteCityName: userData[fieldName],
    }))
  }, [userData])

  return (
    <div className='position-relative'>
      <FloatingLabel label={placeholder} className='mb-3'>
        <Form.Control
          type='text'
          placeholder={placeholder}
          name='autocompleteCityName'
          value={formData.autocompleteCityName}
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
          className='text-danger position-absolute bottom-0 start-50 z-3 translate-middle'
          style={{ fontSize: '.8em' }}
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
              {suggestion.cityState} ({suggestion.province || ''})
            </ListGroup.Item>
          ))}
        </ListGroup>
      )}
    </div>
  )
}

export default FormCityAutocomplete
