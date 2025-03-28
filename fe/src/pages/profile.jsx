import React, {
  useState,
  useEffect,
  createContext,
  use,
  Component,
} from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Card,
  Spinner,
  Row,
  Col,
  Alert,
  Tabs,
  Tab,
  Button,
  Form,
  Container,
  Modal,
} from 'react-bootstrap'
import { FaUser, FaBriefcase, FaFileSignature } from 'react-icons/fa'
import Avatar from '../components/avatar'
import UserDetailForm from '../components/userDetailForm'
import UserJobProfileForm from '../components/userJobProfileForm'
import { USERS, LOGOUT, EMPTY_USER_PROFILE } from '@constants'
import { SERVER_USERS } from '@endpoints'
import { isAuthenticated } from '@utils/auth'
import { getData, postData, putData } from '@utils/api'
import ModalAlert from '../components/modalAlert'
import ProfileSignRequests from '@components/ProfileSignRequests'
import { IoSendSharp } from 'react-icons/io5'
import FormSendSignRequest from '../components/FormSendSignRequest'
import { getDocTemplatesAllowed } from '@utils/auth'
import CustomSpinner from '@components/customSpinner'

export const ProfileContext = createContext()

const Profile = ({ id }) => {
  const [errorMsg, setErrorMsg] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [userData, setUserData] = useState()
  const [isEditable, setIsEditable] = useState(false)
  const [alertMsg, setModalAlert] = useState(['', ''])
  const [showSignRequestModal, setShowSignRequestModal] = useState(false)
  const [originalUserData, setOriginalUserData] = useState()
  const [disableButtons, setDisableButtons] = useState(false)

  // Get the user's allowed templates
  const docTemplatesAllowed = getDocTemplatesAllowed()
  console.log('docTemplatesAllowed: ', docTemplatesAllowed)

  const navigate = useNavigate()

  const contextValue = {
    userData,
    setUserData,
    isEditable,
    setIsEditable,
  }

  // Check if the user is new or not
  // If the id is not a number, it means that the user is new
  let isNewUser = id === 'newuser'.toLowerCase()

  const handleEdit = () => {
    setIsEditable(true)
  }

  const handleCancel = () => {
    setUserData(originalUserData)
    setIsEditable(false)
  }

  const handleTabSelect = (key) => {
    console.log('Selected tab: ', key)
    switch (key) {
      case 'anagrafica':
        setDisableButtons(false)
        break
      case 'jobprofile':
        setDisableButtons(false)
        break
      case 'user-signatures':
        setDisableButtons(true)
        break
      default:
        break
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    // create the body of the request
    const jobProfile = {
      //appUserId: userData.id,
      companyId: userData.jobProfile.company.id,
      branchId: userData.jobProfile.branch.id,
      contractId: userData.jobProfile.contract.id,
      baseSalaryId: userData.jobProfile.baseSalary.id,
      hoursPerWeek: userData.jobProfile.hoursPerWeek,
      jobProfileType: userData.jobProfile.jobProfileType,
      startDate: userData.jobProfile.startDate,
      endDate: userData.jobProfile.endDate,
      jobDescription: userData.jobProfile.jobDescription,
    }

    const apiRequestBody = { ...userData, jobProfile: jobProfile }

    console.log('apiRequestBody: ', apiRequestBody)
    //console.log('User Data: ', userData)

    let apiUrl = ''
    let method = ''

    setIsLoading(true)

    // Determine if it's an update or a new user
    if (userData.id === 0 || userData.id === null) {
      apiUrl = `${SERVER_USERS}/register`
      method = 'POST'
    } else {
      apiUrl = `${SERVER_USERS}/${userData.id}`
      method = 'PUT'
    }

    try {
      let data = {}
      if (method === 'POST') {
        data = await postData(apiUrl, apiRequestBody)
      } else if (method === 'PUT') {
        data = await putData(apiUrl, apiRequestBody)
      } else {
        message = 'Something went wrong! Method not allowed: ' + method
        setModalAlert(['Error', error.message])
      }

      if (data.logout) {
        navigate(LOGOUT)
      } else if (data.isSuccess) {
        console.log('Data: ', data)
        // If all is ok, disable the form
        setModalAlert(['Success', 'Profile updated successfully.'])

        // Update the user data with the new data
        setUserData(data)

        // Redirect to the user profile page
        if (isNewUser) {
          navigate(`${USERS}/${data.id}`)
        }

        setIsEditable(false)
      }
    } catch (error) {
      console.error('Error submitting form:', error)
      // on the forms is preferred the modal to show errors and not the alert.
      setModalAlert(['Error', error.message])
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true)

      try {
        const data = await getData(SERVER_USERS + '/' + id)

        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          setUserData(data)
        }
      } catch (error) {
        setErrorMsg(error.message)
        console.error('Error fetching data: ', error.message)
      } finally {
        setIsLoading(false)
      }
    }
    if (isAuthenticated()) {
      if (isNewUser) {
        console.log('New user')
        setUserData(EMPTY_USER_PROFILE)
        setIsEditable(true)
      } else if (isNaN(Number(id))) {
        // test if the id is a number
        console.log('User not recognized')
        navigate(USERS)
      } else {
        console.log('Fetching user data and render component')
        fetchData()
      }
    }
  }, [id])

  // Save the original data  at the first fetch to be able to reset the form
  useEffect(() => {
    if (userData && originalUserData === undefined) {
      setOriginalUserData(userData)
      console.log('Original user data set:', userData)
    }
  }, [userData, originalUserData])

  return (
    <ProfileContext.Provider value={contextValue}>
      <Container>
        {isLoading && <CustomSpinner />}
        {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
        {<ModalAlert alert={alertMsg} setModalAlert={setModalAlert} />}
        {userData && !isLoading && !errorMsg && (
          <>
            <FormSendSignRequest
              show={showSignRequestModal}
              onHide={() => setShowSignRequestModal(false)}
            />
            <Form onSubmit={handleSubmit}>
              <Card className='w-100'>
                <Card.Body>
                  <Card.Title
                    className='text-center mb-4'
                    style={{ fontSize: '2rem' }}
                  >
                    <Row className='justify-content-start align-content-center'>
                      <Col
                        xs={3}
                        className='justify-content-start align-content-center'
                      >
                        <Avatar
                          sex={userData.sex}
                          id={userData.id}
                          className='rounded-circle img-fluid p-2 shadow'
                          onlySource={false}
                        />
                      </Col>
                      <Col className='d-flex align-content-center'>
                        <div className='d-flex flex-column justify-content-center'>
                          {userData.surnameName} [id: {userData.id}]
                        </div>
                      </Col>
                      <Col className='d-flex justify-content-center align-items-center'>
                        {/*

                        if it's not a newUser and if the app user has some DocAllowedTemplate

                        */}
                        {!isNewUser && docTemplatesAllowed.length > 0 && (
                          <Button
                            variant='warning'
                            className='shadow'
                            onClick={() => setShowSignRequestModal(true)}
                          >
                            Send a Sign Request
                            <IoSendSharp className='ms-2' />
                          </Button>
                        )}
                      </Col>
                    </Row>
                  </Card.Title>

                  {/* TABS  */}

                  <Tabs
                    defaultActiveKey='anagrafica'
                    id='profile-tabs'
                    className='mb-3 fs-3 fw-bold'
                    justify
                    onSelect={(tab) => handleTabSelect(tab)}
                  >
                    <Tab
                      eventKey='anagrafica'
                      title={
                        <>
                          <FaUser className='me-2' /> Anagrafica
                        </>
                      }
                    >
                      <div className='mt-4'>
                        <UserDetailForm userData={userData} />
                      </div>
                    </Tab>

                    <Tab
                      eventKey='jobprofile'
                      title={
                        <>
                          <FaBriefcase className='me-2' /> Profilo lavorativo
                        </>
                      }
                    >
                      <div className='mt-4'>
                        <UserJobProfileForm userData={userData} />
                      </div>
                    </Tab>
                    {!isNewUser &&
                      (userData.signRequestsCreated.length > 0 ||
                        userData.signRequestsSigned.length > 0) && (
                        <Tab
                          eventKey='user-signatures'
                          title={
                            <>
                              <FaFileSignature className='me-2' /> Documenti in
                              firma
                            </>
                          }
                        >
                          <div className='mt-4'>
                            <ProfileSignRequests />
                          </div>
                        </Tab>
                      )}
                  </Tabs>
                </Card.Body>
              </Card>
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
            </Form>
          </>
        )}
      </Container>
    </ProfileContext.Provider>
  )
}

export default Profile
