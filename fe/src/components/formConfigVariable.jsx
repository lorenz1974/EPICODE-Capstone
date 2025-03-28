import React, { useState, useEffect } from 'react'
import {
  Row,
  Col,
  FloatingLabel,
  Form,
  Button,
  Alert,
  Card,
} from 'react-bootstrap'
import { getData, putData, postData } from '@utils/api'
import { SERVER_CONFIG } from '@endpoints'
import ModalAlert from './modalAlert'
import CustomSpinner from './customSpinner'
import { useNavigate } from 'react-router-dom'

const FormConfigVariable = ({ id, onClose }) => {
  const [formData, setFormData] = useState({
    id: 0,
    variableName: '',
    value: '',
    type: '',
    description: '',
    createdAt: '',
    updatedAt: '',
  })

  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [modalAlert, setModalAlert] = useState(['', ''])
  const [isEditable, setIsEditable] = useState(false)

  const navigate = useNavigate()

  const handleEdit = () => setIsEditable(true)
  const handleCancel = () => {
    setIsEditable(false)
    onClose()
  }

  useEffect(() => {
    if (id === 'newvariable') {
      setFormData({
        id: 0,
        variableName: '',
        value: '',
        type: '',
        description: '',
        createdAt: '',
        updatedAt: '',
      })
      setIsEditable(true)
      return
    }

    const fetchVariable = async () => {
      if (!id) return

      setIsLoading(true)
      setErrorMsg('')
      try {
        const data = await getData(`${SERVER_CONFIG}/${id}`)
        const sanitizedData = Object.fromEntries(
          Object.entries(data).map(([key, value]) => [key, value ?? ''])
        )
        setFormData(sanitizedData)
      } catch (error) {
        setErrorMsg(error.message)
        console.error('Error fetching variable:', error.message)
      } finally {
        setIsLoading(false)
      }
    }
    fetchVariable()
  }, [id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async () => {
    let apiUrl = ''
    let method = ''

    setIsLoading(true)
    setErrorMsg('')

    // Determine if it's an update or a new user
    if (formData.id === 0 || formData.id === null) {
      apiUrl = `${SERVER_CONFIG}`
      method = 'POST'
    } else {
      apiUrl = `${SERVER_CONFIG}/${id}`
      method = 'PUT'
    }

    try {
      let data = {}
      if (method === 'POST') {
        data = await postData(apiUrl, formData)
      } else if (method === 'PUT') {
        data = await putData(apiUrl, formData)
      } else {
        message = 'Something went wrong! Method not allowed: ' + method
        setModalAlert(['Error', error.message])
      }

      if (data.logout) {
        navigate(LOGOUT)
      } else if (data.isSuccess) {
        onClose()
      }
    } catch (error) {
      setModalAlert(['Error', 'Error saving variable.'])
      console.error('Error saving variable:', error.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card>
      <Card.Header>
        <h5>
          {id ? `Edit Variable: ${formData.variableName}` : 'New Variable'}
        </h5>
      </Card.Header>
      <Card.Body>
        <Form>
          {isLoading && <CustomSpinner />}
          {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
          <ModalAlert level={modalAlert[0]} message={modalAlert[1]} />

          {!isLoading && !errorMsg && (
            <>
              <Row sm={12}>
                <Col>
                  <FloatingLabel label='Variable Name' className='mb-3'>
                    <Form.Control
                      type='text'
                      name='variableName'
                      value={formData.variableName}
                      onChange={handleChange}
                      disabled={!isEditable}
                    />
                  </FloatingLabel>
                  <Alert variant='info' className='mt-1 p-1'>
                    <h5 style={{ fontSize: '.8em' }}>variableName</h5>
                    <p style={{ fontSize: '.8em' }}>
                      The unique name of the configuration variable.
                    </p>
                  </Alert>
                </Col>
              </Row>

              <Row sm={12}>
                <Col>
                  <FloatingLabel label='Value' className='mb-3'>
                    <Form.Control
                      as='textarea'
                      name='value'
                      value={formData.value}
                      onChange={handleChange}
                      style={{ height: '8em' }}
                      disabled={!isEditable}
                    />
                  </FloatingLabel>
                  <Alert variant='info' className='mt-1 p-1'>
                    <h5 style={{ fontSize: '.8em' }}>value</h5>
                    <p style={{ fontSize: '.8em' }}>
                      The value of the configuration variable.
                    </p>
                  </Alert>
                </Col>
              </Row>

              <Row sm={12}>
                <Col>
                  <FloatingLabel label='Type' className='mb-3'>
                    <Form.Control
                      type='text'
                      name='type'
                      value={formData.type}
                      onChange={handleChange}
                      disabled
                    />
                  </FloatingLabel>
                  <Alert variant='info' className='mt-1 p-1'>
                    <h5 style={{ fontSize: '.8em' }}>type</h5>
                    <p style={{ fontSize: '.8em' }}>
                      The data type of the configuration variable.
                    </p>
                  </Alert>
                </Col>
              </Row>

              <Row sm={12}>
                <Col>
                  <FloatingLabel label='Description' className='mb-3'>
                    <Form.Control
                      as='textarea'
                      name='description'
                      value={formData.description}
                      onChange={handleChange}
                      style={{ height: '8em' }}
                      disabled={!isEditable}
                    />
                  </FloatingLabel>
                  <Alert variant='info' className='mt-1 p-1'>
                    <h5 style={{ fontSize: '.8em' }}>description</h5>
                    <p style={{ fontSize: '.8em' }}>
                      A description of the configuration variable.
                    </p>
                  </Alert>
                </Col>
              </Row>
            </>
          )}
        </Form>
      </Card.Body>
      <Card.Footer>
        <div className='d-flex justify-content-end mt-3'>
          {!isEditable ? (
            <Button
              type='button'
              className='btn btn-primary me-2'
              onClick={handleEdit}
            >
              Edit
            </Button>
          ) : (
            <>
              <Button
                type='button'
                className='btn btn-secondary me-2'
                onClick={handleCancel}
              >
                Cancel
              </Button>
              <Button
                type='button'
                className='btn btn-success'
                onClick={handleSubmit}
              >
                Save
              </Button>
            </>
          )}
        </div>
      </Card.Footer>
    </Card>
  )
}

export default FormConfigVariable
