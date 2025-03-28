import React, { useState, useEffect, useContext } from 'react'
import { Form, Button, Spinner, Alert, Modal, Row, Col } from 'react-bootstrap'
import { postData } from '@utils/api'
import { SERVER_SIGN_REQUEST } from '@endpoints'
import { ProfileContext } from '@pages/profile'
import { getDocTemplatesAllowed } from '@utils/auth'

const FormSendSignRequest = ({ show, onHide }) => {
  const { userData } = useContext(ProfileContext)

  const [selectedTemplate, setSelectedTemplate] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [successMsg, setSuccessMsg] = useState('')
  const [submitDisabled, setSubmitDisabled] = useState(false)

  // Get the user's allowed templates
  const docTemplatesAllowed = getDocTemplatesAllowed()

  // Set the first template as the default selection
  useEffect(() => {
    if (docTemplatesAllowed.length > 0) {
      setSelectedTemplate(docTemplatesAllowed[0])
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!selectedTemplate) {
      setErrorMsg('Please select a document template.')
      return
    }

    // Disable the submit button to prevent multiple requests
    setSubmitDisabled(true)

    const requestBody = {
      signedByAppUserId: userData.id,
      docTemplateId: selectedTemplate.id,
    }

    setIsLoading(true)
    setErrorMsg('')
    setSuccessMsg('')
    try {
      const response = await postData(SERVER_SIGN_REQUEST, requestBody)
      if (response.isSuccess) {
        setSuccessMsg('Sign request sent successfully.')
      } else {
        setErrorMsg('Failed to send sign request.')
      }
    } catch (error) {
      setErrorMsg('Error sending sign request: ' + error.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Modal
      show={show}
      onHide={onHide}
      size='lg'
      aria-labelledby='contained-modal-title-vcenter'
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id='contained-modal-title-vcenter'>
          Send Sign Request
        </Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          <Form.Group>
            <Row>
              <Col xs={12} md={6} className='mb-3'>
                <Form.Label className='fw-bold'>
                  Select a Document Template
                </Form.Label>
                {docTemplatesAllowed.map((template) => (
                  <Form.Check
                    key={template.id}
                    type='radio'
                    name='docTemplate'
                    label={template.name}
                    onChange={() => setSelectedTemplate(template)}
                    checked={selectedTemplate?.id === template.id}
                  />
                ))}
              </Col>
              <Col>
                <h5>Template Description:</h5>
                {selectedTemplate && (
                  <div className='mt-3'>
                    <div
                      dangerouslySetInnerHTML={{
                        __html: selectedTemplate.description,
                      }}
                    />
                  </div>
                )}
              </Col>
            </Row>
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant='secondary'
            // it's only disabled when the form is submitting
            // UX could be worse if it's disabled when there's an error
            disable={`${submitDisabled}`}
            onClick={onHide}
          >
            Close
          </Button>
          <Button
            variant='danger'
            type='submit'
            onClick={(e) => handleSubmit(e)}
            hidden={submitDisabled}
          >
            Send
          </Button>
        </Modal.Footer>
        {isLoading && (
          <div className='d-flex justify-content-center align-content-center m-2'>
            <Spinner />
          </div>
        )}
        {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
        {successMsg && <Alert variant='success'>{successMsg}</Alert>}
      </Form>
    </Modal>
  )
}

export default FormSendSignRequest
