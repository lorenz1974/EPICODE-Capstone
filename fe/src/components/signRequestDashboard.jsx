import React, { useEffect, useState } from 'react'
import { Row, Col, Container, Spinner, Alert } from 'react-bootstrap'
import SignRequestStatusCard from './signRequestStatusCard'
import { SERVER_SIGN_REQUEST_STATS } from '@endpoints'
import { getData } from '@utils/api'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'

const SignRequestDashboard = ({ userId }) => {
  const [statusArray, setStatusArray] = useState([])
  const [errorMsg, setErrorMsg] = useState(null)
  const [isLoading, setIsLoading] = useState(false)

  const navigate = useNavigate()

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true)
      try {
        const url = userId
          ? `${SERVER_SIGN_REQUEST_STATS}?userId=${userId}`
          : SERVER_SIGN_REQUEST_STATS
        const data = await getData(url)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          setStatusArray(data.map((item) => ({ [item.status]: item.count })))
        } else {
          setErrorMsg('Failed to fetch sign request stats')
        }
      } catch (error) {
        setErrorMsg(error.message)
        console.error('Error fetching sign request stats:', error.message)
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }, [userId])

  return (
    <Container className='mt-4 d-flex justify-content-center align-content-center'>
      {isLoading && (
        <div>
          <Spinner />
        </div>
      )}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {statusArray.length > 0 && !isLoading && !errorMsg && (
        <Row className=' justify-content-center g-3 w-100'>
          {statusArray.map((statusObj, index) => {
            const status = Object.keys(statusObj)[0]
            return (
              <Col key={index} xs={12} sm={6} md={4} lg={3}>
                <SignRequestStatusCard
                  status={status}
                  statusArray={statusArray}
                />
              </Col>
            )
          })}
        </Row>
      )}
    </Container>
  )
}

export default SignRequestDashboard
