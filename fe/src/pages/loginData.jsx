import React, { useState, useEffect, useContext } from 'react'
import { Container, Spinner, Alert, Row, Col } from 'react-bootstrap'
import { SERVER_LOGIN_DATA } from '@endpoints'
import { getData } from '@utils/api'
import LoginDataTable from '@components/LoginDataTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'

const LoginData = () => {
  const navigate = useNavigate()
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [loginData, setLoginData] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchLoginData = async () => {
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
        const data = await getData(SERVER_LOGIN_DATA, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setLoginData(data._embedded.loginDataList)
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

    fetchLoginData()
  }, [pagination.currentPage, q])

  return (
    <Container
      id='login-data'
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {loginData && !isLoading && !errorMsg && (
        <Row>
          <Col>
            <FormDivider title='Login Data' />
            <LoginDataTable data={loginData} />
            <Pagination />
          </Col>
        </Row>
      )}
    </Container>
  )
}

export default LoginData
