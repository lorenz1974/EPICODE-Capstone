import React from 'react'
import { Context } from '../App'
import { useContext, useState, setState, useEffect, useRef } from 'react'
import {
  Alert,
  Container,
  Card,
  FloatingLabel,
  Form,
  Button,
  Spinner,
} from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import { FaRegUserCircle } from 'react-icons/fa'
import { postData } from '@utils/api'
import { SERVER_LOGIN } from '@endpoints'
import { TYPE } from '@constants'
import { logout, isAuthenticated, saveUser } from '@utils/auth'
import { validateInputs } from '@utils/validators'
import { msgResponse } from '@utils/functions'

const Login = () => {
  const { setAuthenticated } = useContext(Context)

  const [isChecked, setIsChecked] = useState(false)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [isAuth, setIsAuth] = useState(isAuthenticated())
  const [submitDisabled, setSubmitDisabled] = useState(false)

  const Navigate = useNavigate()

  useEffect(() => {
    if (isAuth) {
      Navigate('/')
    }
  }, [isAuth])

  useEffect(() => {
    localStorage.removeItem(TYPE)
  }, [])

  const remember = () => {
    setIsChecked(!isChecked)
    if (isChecked) localStorage.setItem(TYPE, 'local')
    else localStorage.setItem(TYPE, 'session')
  }

  const logIn = async (e) => {
    setErrorMsg('')
    setSubmitDisabled(true)
    setIsLoading(true)

    remember()

    e.preventDefault()
    const validResponse = validation()
    if (!validResponse.isSuccess) {
      setErrorMsg(validResponse.message)
      setSubmitDisabled(false)
      setIsLoading(false)
      return
    }
    const response = await fillStorage()
    if (response.isSuccess) {
      setIsAuth(true)
      setAuthenticated(true)
    } else {
      setErrorMsg(response.message)
    }
    setSubmitDisabled(false)
    setIsLoading(false)
  }

  const validation = () => {
    let validate = ''
    validate = validateInputs(username, 'name')
    if (validate !== '') {
      return msgResponse(false, validate)
    }
    validate = validateInputs(password, 'password')
    if (validate !== '') {
      return msgResponse(false, validate)
    }
    return msgResponse()
  }

  const fillStorage = async () => {
    try {
      const body = {
        username: username,
        password: password,
      }
      const response = await postData(SERVER_LOGIN, body)
      console.log('RESPONSE: ', response)
      if (response.token) {
        saveUser(response.token, response.nameSurname, response)
        return msgResponse()
      }
    } catch (error) {
      console.log('ERROR: ', error)
      return msgResponse(false, error.message)
    }
  }

  return (
    <Container
      id='container-login'
      className='d-flex justify-content-center align-items-center position-fixed top-50 start-50 translate-middle'
    >
      <form onSubmit={(e) => logIn(e)}>
        <Card style={{ width: '24rem' }}>
          <Card.Body>
            <Card.Title
              className='text-center mb-4'
              style={{ fontSize: '3rem' }}
            >
              {<FaRegUserCircle />}
              <h2 className='text-center mb-4'>Login</h2>
            </Card.Title>
            <FloatingLabel label='Username' className='mb-3'>
              <Form.Control
                type='text'
                placeholder='Username'
                id='username'
                name='username'
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </FloatingLabel>
            <FloatingLabel label='Password' className='mb-3'>
              <Form.Control
                type='password'
                placeholder='Password'
                id='password'
                name='password'
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </FloatingLabel>
            <div className='form-check mt-3 d-flex justify-content-start'>
              <input
                type='checkbox'
                className='form-check-input me-1'
                id='remember'
                onChange={remember}
              />
              <label className='form-check-label' htmlFor='remember'>
                Ricordami
              </label>
            </div>
            <Button
              type='submit'
              disable={`${submitDisabled}`}
              className='btn btn-primary w-100 mt-3'
            >
              Login
              <Spinner
                animation='border'
                size='sm'
                hidden={!isLoading}
                className='ms-2'
              />
            </Button>
            {errorMsg !== '' && (
              <Alert variant='danger' className='mt-3'>
                {errorMsg}
              </Alert>
            )}
          </Card.Body>
        </Card>
      </form>
    </Container>
  )
}

export default Login
