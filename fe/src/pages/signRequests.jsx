import React, { useState, useEffect, useContext } from 'react'
import { Container, Spinner, Alert, Row, Col, Tab, Tabs } from 'react-bootstrap'
import { SERVER_SIGN_REQUEST } from '@endpoints'
import { getData } from '@utils/api'
import SignRequestTable from '@components/SignRequestTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'
import SignRequestDashboard from '@components/SignRequestDashboard'
import CustomSpinner from '@components/customSpinner'

const SignRequests = () => {
  const navigate = useNavigate()
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [signRequests, setSignRequests] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchSignRequests = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 25,
          sortBy: 'createdAt',
          direction: 'DESC',
          alsoDeleted: false,
          q: q,
        }
        const data = await getData(SERVER_SIGN_REQUEST, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setSignRequests(data._embedded.signRequestList)
            setPagination({
              totalPages: data.page.totalPages,
              currentPage: data.page.number,
              pageSize: data.page.size,
              totalElements: data.page.totalElements,
            })
          } else {
            setErrorMsg('No data found')
          }
        }
      } catch (error) {
        setErrorMsg(error.message)
        console.error('Error fetching data: ', error.message)
      } finally {
        setIsLoading(false)
      }
    }

    fetchSignRequests()
  }, [pagination.currentPage, q])

  return (
    <Container
      id='sign-request'
      className='d-flex flex-column justify-content-start align-content-center'
      fluid
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {signRequests && !isLoading && !errorMsg && (
        <div>
          <FormDivider title='Sign Requests' />
          <SignRequestTable requests={signRequests} />
          <Pagination />
        </div>
      )}
    </Container>
  )
}

export default SignRequests
