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
import { getData, postData } from '@utils/api'
import { SERVER_DOC_TEMPLATES } from '@endpoints'
import ModalAlert from './modalAlert'
import CustomSpinner from './customSpinner'
import FormDivider from './formDivider'

const FormDocTemplate = ({ id, onClose }) => {
  const [formData, setFormData] = useState({
    id: 0,
    name: '',
    subject: '',
    description: '',
    templateJson: '',
    dataTemplateClass: '',
    templatePath: '',
    templateProviderFileApiEndpoint: '',
    templateProviderFileIdField: '',
    templateSqlQuery: '',
    templateProvider: '',
    templateProviderApiEndpoint: '',
    templateApiCrudType: '',
    templateProviderApiEndpointIdField: '',
    templateApiCrudCheckEndpoint: '',
    templateApiCrudCheckType: '',
    templateApiCrudCheckStatusField: '',
    templateApiCrudCheckCompletedValues: [],
    templateApiCrudCheckFailedValues: [],
    templateApiCrudCheckWaitingValues: [],
    templateApiCrudErrorField: '',
    templateApiCrudErrorMessageField: '',
    templateApiCrudErrorValues: [],
    createdAt: '',
    updatedAt: '',
    deleted: false,
  })

  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [modalAlert, setModalAlert] = useState(['', ''])
  const [isEditable, setIsEditable] = useState(false)
  const [disableButtons, setDisableButtons] = useState(false)

  const bigTextAreaStyle = { height: '30em' }

  useEffect(() => {
    const fetchTemplate = async () => {
      if (id === 'newform') return
      setIsLoading(true)
      setErrorMsg('')
      try {
        const data = await getData(`${SERVER_DOC_TEMPLATES}/${id}`)
        const sanitizedData = Object.fromEntries(
          Object.entries(data).map(([key, value]) => [key, value ?? ''])
        )
        setFormData(sanitizedData)
      } catch (error) {
        setErrorMsg(error.message)
        console.error('Error fetching template:', error.message)
      } finally {
        setIsLoading(false)
      }
    }
    fetchTemplate()
  }, [id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleArrayChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value
        .split('\n')
        .map((item) => item.trim())
        .filter(Boolean),
    }))
  }

  const handleSubmit = async () => {
    setIsLoading(true)
    setErrorMsg('')
    try {
      const formattedData = {
        ...formData,
        templateApiCrudCheckCompletedValues:
          formData.templateApiCrudCheckCompletedValues.join('\n'),
        templateApiCrudCheckFailedValues:
          formData.templateApiCrudCheckFailedValues.join('\n'),
        templateApiCrudCheckWaitingValues:
          formData.templateApiCrudCheckWaitingValues.join('\n'),
        templateApiCrudErrorValues:
          formData.templateApiCrudErrorValues.join('\n'),
      }
      await postData(SERVER_DOC_TEMPLATES, formattedData)
      onClose()
    } catch (error) {
      setErrorMsg(error.message)
      setModalAlert(['Error', 'Error saving template.'])
      console.error('Error saving template:', error.message)
    } finally {
      setIsLoading(false)
    }
  }

  const handleEdit = () => setIsEditable(true)
  const handleCancel = () => {
    setIsEditable(false)
    onClose()
  }
  const handleReset = () => {
    setFormData({
      id: 0,
      name: '',
      subject: '',
      description: '',
      templateJson: '',
      dataTemplateClass: '',
      templatePath: '',
      templateProviderFileApiEndpoint: '',
      templateProviderFileIdField: '',
      templateSqlQuery: '',
      templateProvider: '',
      templateProviderApiEndpoint: '',
      templateApiCrudType: '',
      templateProviderApiEndpointIdField: '',
      templateApiCrudCheckEndpoint: '',
      templateApiCrudCheckType: '',
      templateApiCrudCheckStatusField: '',
      templateApiCrudCheckCompletedValues: [],
      templateApiCrudCheckFailedValues: [],
      templateApiCrudCheckWaitingValues: [],
      templateApiCrudErrorField: '',
      templateApiCrudErrorMessageField: '',
      templateApiCrudErrorValues: [],
      createdAt: '',
      updatedAt: '',
      deleted: false,
    })
  }

  return (
    <>
      <Card>
        <Card.Header>
          <h5>{id === 'newform' ? 'Create New Template' : 'Edit Template'}</h5>
        </Card.Header>
        <Card.Body>
          <Form>
            {isLoading && <CustomSpinner />}
            {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
            <ModalAlert level={modalAlert[0]} message={modalAlert[1]} />

            {!isLoading && !errorMsg && (
              <>
                <Row>
                  <Col xs={12}>
                    <FormDivider title='Template Configuration' />
                    <FloatingLabel label='Name' className='mb-3'>
                      <Form.Control
                        type='text'
                        name='name'
                        value={formData.name}
                        onChange={handleChange}
                        required
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>name</h5>
                      <p style={{ fontSize: '.8em' }}>
                        This is the name of the document template. Provide a
                        descriptive name.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={12}>
                    <FloatingLabel label='Subject' className='mb-3'>
                      <Form.Control
                        type='text'
                        name='subject'
                        value={formData.subject}
                        onChange={handleChange}
                        required
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>subject</h5>
                      <p style={{ fontSize: '.8em' }}>
                        This is the subject of the document template. This will
                        be the subject of the email sent to the signer.{' '}
                        <b>
                          Integrate the placeholders for the field of the query
                          surrounded by two &#123;&#123;&#125;&#125;
                        </b>
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={12}>
                    <FloatingLabel label='Description' className='mb-3'>
                      <Form.Control
                        as='textarea'
                        name='description'
                        value={formData.description}
                        onChange={handleChange}
                        style={{
                          height: '8em',
                          whiteSpace: 'pre-wrap',
                          wordWrap: 'break-word',
                        }}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>description</h5>
                      <p style={{ fontSize: '.8em' }}>
                        This field corresponds to the description that the user
                        sending the signature request will see as the template
                        description. It is possible to integrate HTML elements
                        (e.g., <b>bold</b>) to highlight certain parts.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel label='Data Template Class' className='mb-3'>
                      <Form.Control
                        type='text'
                        name='dataTemplateClass'
                        value={formData.dataTemplateClass}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>dataTemplateClass</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the class used for data templating. This is used
                        to generate the template JSON. It's not strickly
                        necessary and it must be integrated in the application
                        code.{' '}
                        <b>This feature is not implemented at the moment.</b>
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel label='Template Path' className='mb-3'>
                      <Form.Control
                        type='text'
                        name='templatePath'
                        value={formData.templatePath}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>templatePath</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Provide the file path where the template is stored. It
                        mut refer to the relative file path in the application.
                        It is the part of the path{' '}
                        <b>
                          after the 'fileArchiveRootPath' in the configuration.
                        </b>
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={12}>
                    <FloatingLabel label='Template SQL Query' className='mb-3'>
                      <Form.Control
                        as='textarea'
                        name='templateSqlQuery'
                        value={formData.templateSqlQuery}
                        onChange={handleChange}
                        style={bigTextAreaStyle}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>templateSqlQuery</h5>
                      <p style={{ fontSize: '.8em' }}>
                        This field must contains the SQL query to retrieve the
                        data to be used in the template. It could use all the
                        datasource comfigured at the application level. All the
                        fields extracted could be used in the template JSON.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <FormDivider title='Template Provider Configuration' />
                  <Col xs={6}>
                    <FloatingLabel label='Template Provider' className='mb-3'>
                      <Form.Control
                        type='text'
                        name='templateProvider'
                        value={formData.templateProvider}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>templateProvider</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the provider for the template. The name of the
                        provider must be the same as the one in the
                        configuration of the application. The provider is the
                        one that will be used to send the template{' '}
                        <b>
                          using a token stored in the configuration in a
                          variable with the name
                          &#60;providername.lowercase&#62;Token
                        </b>
                        .
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={12}>
                    <FloatingLabel label='Template JSON' className='mb-3'>
                      <Form.Control
                        as='textarea'
                        name='templateJson'
                        value={formData.templateJson}
                        onChange={handleChange}
                        style={bigTextAreaStyle}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>templateJson</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Provide the JSON structure for the template.{' '}
                        <b>
                          This is the most important part of the template an
                          MUST contain the placeholders for the fields of the
                          query
                        </b>
                        . The placeholders must be surrounded by two
                        &#123;&#123;&#125;&#125; and must be like these:
                        "Email": "&#123;&#123; signeremail &#125;&#125;",
                        "GivenName": " &#123;&#123; signername &#125;&#125;",
                        "Surname": " &#123;&#123; signersurname &#125;&#125;",
                        "PhoneNumber": "&#123;&#123; signercellphone
                        &#125;&#125; ". Usually the remote sign provider release
                        a template after the service configuration on its
                        platform.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <FormDivider title='Provider API Configuration' />
                  <Col xs={6}>
                    <FloatingLabel
                      label='Template Provider FILE API Endpoint'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateProviderFileApiEndpoint'
                        value={formData.templateProviderFileApiEndpoint}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateProviderFileApiEndpoint
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the API endpoint of the provider where send the{' '}
                        <b>FILE</b>. This is the endpoint{' '}
                        <b>where the document to be signed must be uploaded</b>{' '}
                        typically with a POST crud.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template Provider ENVELOPE API Endpoint'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateProviderApiEndpoint'
                        value={formData.templateProviderApiEndpoint}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateProviderApiEndpoint
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the API endpoint of the provider where send the{' '}
                        <b>sign request</b> (JSON data). This is the endpoint{' '}
                        <b>where the sign request must be sent</b> typically
                        with a POST crud.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Type'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudType'
                        value={formData.templateApiCrudType}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>templateApiCrudType</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the CRUD type for the template API.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template Provider API Endpoint ID Field'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateProviderApiEndpointIdField'
                        value={formData.templateProviderApiEndpointIdField}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateProviderApiEndpointIdField
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify{' '}
                        <b>
                          the field that contains the envelope ID in the
                          provider response when a sign request is sent
                        </b>
                        . The value of this field will be stored in the sign
                        request record to check the status of the sign request.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <FormDivider title='Envelope Status Configuration' />
                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Endpoint'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudCheckEndpoint'
                        value={formData.templateApiCrudCheckEndpoint}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckEndpoint
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the endpoint for checking CRUD operations on the
                        template API.
                      </p>
                    </Alert>
                  </Col>
                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Type'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudCheckType'
                        value={formData.templateApiCrudCheckType}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckType
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the type of CRUD check for the template API.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Status Field'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudCheckStatusField'
                        value={formData.templateApiCrudCheckStatusField}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckStatusField
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the{' '}
                        <b>
                          name of status field that contains the status of the
                          sign request in the provider response when a sign
                          request is sent
                        </b>
                        . The value of this field will be used to check the
                        status of the sign request using the endpoint configured
                        in the templateApiCrudCheckEndpoint.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Completed Values (newline-separated)'
                      className='mb-3'
                    >
                      <Form.Control
                        as='textarea'
                        name='templateApiCrudCheckCompletedValues'
                        value={formData.templateApiCrudCheckCompletedValues.join(
                          '\n'
                        )}
                        onChange={handleArrayChange}
                        style={{ height: '8em' }}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckCompletedValues
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        <b>
                          Specify the values that indicate that the sign request
                          is COMPLETED
                        </b>
                        . Could be specified multiple values{' '}
                        <b>separated by newlines</b>. These values are searched
                        in the field templateApiCrudCheckStatusField of the
                        provider response checked at this url
                        templateApiCrudCheckEndpoint.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Failed Values (newline-separated)'
                      className='mb-3'
                    >
                      <Form.Control
                        as='textarea'
                        name='templateApiCrudCheckFailedValues'
                        value={formData.templateApiCrudCheckFailedValues.join(
                          '\n'
                        )}
                        onChange={handleArrayChange}
                        style={{ height: '8em' }}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckFailedValues
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        <b>
                          Specify the values that indicate that the sign request
                          is FAILED
                        </b>
                        . Could be specified multiple values{' '}
                        <b>separated by newlines</b>. These values are searched
                        in the field templateApiCrudCheckStatusField of the
                        provider response checked at this url
                        templateApiCrudCheckEndpoint.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Check Waiting Values (newline-separated)'
                      className='mb-3'
                    >
                      <Form.Control
                        as='textarea'
                        name='templateApiCrudCheckWaitingValues'
                        value={formData.templateApiCrudCheckWaitingValues.join(
                          '\n'
                        )}
                        onChange={handleArrayChange}
                        style={{ height: '8em' }}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudCheckWaitingValues
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        <b>
                          Specify the values that indicate that the sign request
                          is WAITING for the signer to sign.
                        </b>
                        . Could be specified multiple values{' '}
                        <b>separated by newlines</b>. These values are searched
                        in the field templateApiCrudCheckStatusField of the
                        provider response checked at this url
                        templateApiCrudCheckEndpoint.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Error Field'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudErrorField'
                        value={formData.templateApiCrudErrorField}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudErrorField
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the error field for CRUD operations on the
                        template API.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Error Values (newline-separated)'
                      className='mb-3'
                    >
                      <Form.Control
                        as='textarea'
                        name='templateApiCrudErrorValues'
                        value={formData.templateApiCrudErrorValues.join('\n')}
                        onChange={handleArrayChange}
                        style={{ height: '8em' }}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudErrorValues
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the error values for CRUD operations, separated
                        by newlines.
                      </p>
                    </Alert>
                  </Col>

                  <Col xs={6}>
                    <FloatingLabel
                      label='Template API CRUD Error Message Field'
                      className='mb-3'
                    >
                      <Form.Control
                        type='text'
                        name='templateApiCrudErrorMessageField'
                        value={formData.templateApiCrudErrorMessageField}
                        onChange={handleChange}
                        disabled={!isEditable}
                      />
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>
                        templateApiCrudErrorMessageField
                      </h5>
                      <p style={{ fontSize: '.8em' }}>
                        Specify the error message field for CRUD operations on
                        the template API.
                      </p>
                    </Alert>
                  </Col>
                </Row>
                <Row>
                  <Col xs={6}>
                    <FloatingLabel label='Deleted' className='mb-3'>
                      <Form.Select
                        name='deleted'
                        value={formData.deleted ? 'true' : 'false'}
                        onChange={(e) =>
                          handleChange({
                            target: {
                              name: 'deleted',
                              value: e.target.value === 'true',
                            },
                          })
                        }
                        disabled={!isEditable}
                      >
                        <option value='false'>False</option>
                        <option value='true'>True</option>
                      </Form.Select>
                    </FloatingLabel>
                    <Alert variant='info' className='mt-1 p-1'>
                      <h5 style={{ fontSize: '.8em' }}>deleted</h5>
                      <p style={{ fontSize: '.8em' }}>
                        Indicates whether the template is marked as deleted.
                      </p>
                    </Alert>
                  </Col>
                </Row>
              </>
            )}
          </Form>
        </Card.Body>
        <Card.Footer>
          {!disableButtons && (
            <div className='d-flex justify-content-end mt-3'>
              {!isEditable ? (
                <Button
                  type='button'
                  className='btn btn-primary me-2'
                  onClick={handleEdit}
                >
                  Modifica
                </Button>
              ) : (
                <>
                  <Button
                    type='button'
                    className='btn btn-secondary me-2'
                    onClick={handleCancel}
                  >
                    Annulla
                  </Button>
                  <Button
                    type='button'
                    className='btn btn-success'
                    onClick={handleSubmit}
                  >
                    Invia
                  </Button>
                </>
              )}
            </div>
          )}
        </Card.Footer>
      </Card>{' '}
    </>
  )
}

export default FormDocTemplate
