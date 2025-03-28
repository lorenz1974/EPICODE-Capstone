import React from 'react'
import { useContext } from 'react'
import SignRequestTable from './signRequestTable'
import SignRequestDashboard from './signRequestDashboard'
import { Tabs, Tab, Container } from 'react-bootstrap'
import { ProfileContext } from '@pages/profile'
import { FaFileSignature, FaCheckCircle } from 'react-icons/fa'
import { RiDashboard3Fill } from 'react-icons/ri'

const ProfileSignRequests = () => {
  const { userData, setUserData } = useContext(ProfileContext)
  return (
    <Container className='mt-4 w-100'>
      <Tabs
        defaultActiveKey='dashboard'
        id='sign-requests-tabs'
        className='mb-3 fs-3 fw-bold'
        justify
      >
        <Tab
          eventKey='dashboard'
          title={
            <>
              <RiDashboard3Fill className='me-2' /> Dashboard
            </>
          }
        >
          <SignRequestDashboard userId={userData.id} />
        </Tab>
        <Tab
          eventKey='signs-requested'
          title={
            <>
              <FaFileSignature className='me-2' /> Signs Requested
            </>
          }
        >
          <SignRequestTable signed={false} />
        </Tab>
        <Tab
          eventKey='documents-signed'
          title={
            <>
              <FaCheckCircle className='me-2' /> Documents Signed
            </>
          }
        >
          <SignRequestTable signed={true} />
        </Tab>
      </Tabs>
    </Container>
  )
}

export default ProfileSignRequests
