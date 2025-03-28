import React, { useState, useEffect, useContext } from 'react'
import { Container, Spinner, Alert, Row, Col } from 'react-bootstrap'
import { SERVER_SIGN_REQUEST_CHRONOLOGIES } from '@endpoints'
import { getData } from '@utils/api'
import SignRequestChronologiesTable from '@components/SignRequestChronologiesTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'
import CustomSpinner from '@components/customSpinner'

const SignRequestChronologies = () => {
  const navigate = useNavigate()
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [chronologies, setChronologies] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchChronologies = async () => {
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
        const data = await getData(SERVER_SIGN_REQUEST_CHRONOLOGIES, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setChronologies(data._embedded.signRequestChronologyList)
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

    fetchChronologies()
  }, [pagination.currentPage, q])

  return (
    <Container
      id='sign-request-chronologies'
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {chronologies && !isLoading && !errorMsg && (
        <Row>
          <Col>
            <FormDivider title='Sign Request Chronologies' />
            <SignRequestChronologiesTable chronologies={chronologies} />
            <Pagination />
          </Col>
        </Row>
      )}
    </Container>
  )
}

export default SignRequestChronologies
