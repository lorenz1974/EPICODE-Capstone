import React, { useContext } from 'react'
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'
import { Context } from '../App'
import { Row, Col, Container, Alert } from 'react-bootstrap'
import { SERVER_USERS } from '@endpoints'
import { getData } from '@utils/api'
import { isAuthenticated } from '@utils/auth'
import UserCard from '@components/UserCard'
import Pagination from '@components/pagination'
import CustomSpinner from '@components/customSpinner'

const Users = () => {
  const { q, setQ } = useContext(Context)
  const { pagination, setPagination } = useContext(Context)
  const { authenticated, setAuthenticated } = useContext(Context)

  const [errorMsg, setErrorMsg] = useState('')
  const [userData, setUserData] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  const navigate = useNavigate()

  useEffect(() => {
    const fetchUsers = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 25,
          sortBy: 'surname',
          direction: 'ASC',
          alsoDeleted: false,
          q: q,
        }
        const data = await getData(SERVER_USERS, params)
        if (data.logout) {
          navigate(LOGOUT) // Redirect to LOGOUT route
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setUserData(data._embedded.appUserGetAllResponseList)

            setPagination({
              totalPages: data.page.totalPages,
              currentPage: data.page.number,
              pageSize: data.page.size,
              totalElements: data.page.totalElements,
            })

            console.log('Fetched Users: ', data)
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
    if (isAuthenticated()) {
      fetchUsers()
    }
  }, [pagination.currentPage, q])

  return (
    <Container
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}

      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {userData && !isLoading && !errorMsg && (
        <Container>
          <Row
            xs={1}
            sm={2}
            md={3}
            lg={4}
            xl={5}
            className=' justify-content-start align-content-start g-3'
          >
            {userData.map((user) => (
              <Col
                id={`user-col-` + user.id}
                key={`user-col-` + user.id}
                className='d-flex flex-column'
              >
                <UserCard {...user} />
              </Col>
            ))}
          </Row>

          <Pagination />
        </Container>
      )}
    </Container>
  )
}

export default Users
