import React from 'react'
import { useState, useEffect, useContext } from 'react'
import { Row, Col, Container, Spinner, Alert } from 'react-bootstrap'
import { SERVER_COMPANIES } from '@endpoints'
import { getData } from '@utils/api'
import { isAuthenticated } from '@utils/auth'
import CompanyCard from '@components/CompanyCard'
import Pagination from '@components/pagination'
import { Context } from '../App'
import CustomSpinner from '@components/customSpinner'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'

const Companies = () => {
  const { q, setQ } = useContext(Context)
  const { pagination, setPagination } = useContext(Context)

  const [errorMsg, setErrorMsg] = useState('')
  const [companyData, setCompanyData] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  const navigate = useNavigate()

  useEffect(() => {
    const fetchCompanies = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 10, // pagination.pageSize,
          sortBy: 'description',
          direction: 'ASC',
          alsoDeleted: false,
          q: q,
        }
        const data = await getData(SERVER_COMPANIES, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          setIsLoading(false)
          if (data.page.totalElements !== 0) {
            setCompanyData(data._embedded.companyList)

            setPagination((prev) => ({
              ...prev,
              totalPages: data.page.totalPages,
              currentPage: data.page.number,
              pageSize: data.page.size,
              totalElements: data.page.totalElements,
            }))

            console.log('Fetched Companies: ', data)
          } else {
            setErrorMsg('No data found')
          }
        }
      } catch (error) {
        setIsLoading(false)
        setErrorMsg(error.message)
        console.error('Error fetching data: ', error.message)
      }
    }
    if (isAuthenticated()) {
      fetchCompanies()
    }
  }, [pagination.currentPage, q])

  return (
    <Container
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {companyData && !isLoading && !errorMsg && (
        <Container>
          <Row
            xs={1}
            className=' justify-content-start align-content-start g-3'
          >
            {companyData.map((company) => (
              <Col
                id={`company-col-` + company.id}
                key={`company-col-` + company.id}
                className='d-flex flex-column'
              >
                <CompanyCard {...company} />
              </Col>
            ))}
          </Row>
          <Pagination />
        </Container>
      )}
    </Container>
  )
}

export default Companies
