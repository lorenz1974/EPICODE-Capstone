import React, { useState, useEffect, useContext } from 'react'
import { Container, Alert, Row, Col } from 'react-bootstrap'
import { SERVER_CONFIG } from '@endpoints'
import { CONFIG } from '@constants'
import { getData } from '@utils/api'
import ConfigTable from '@components/ConfigTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate, useParams } from 'react-router-dom'
import { LOGOUT } from '@constants'
import CustomSpinner from '@components/customSpinner'
import FormConfigVariable from '@components/formConfigVariable'

const Config = () => {
  const navigate = useNavigate()
  const { id } = useParams() // Get the variable name from the URL
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [configVariables, setConfigVariables] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchConfigVariables = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 25,
          sortBy: 'id',
          direction: 'ASC',
          q: q,
        }
        const data = await getData(SERVER_CONFIG, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setConfigVariables(data._embedded.configVariableList)
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

    if (!id) {
      fetchConfigVariables()
    }
  }, [pagination.currentPage, q, id])

  if (id) {
    return (
      <Container>
        <FormConfigVariable
          id={id === 'newvariable' ? '' : id}
          onClose={() => navigate(CONFIG)}
        />
      </Container>
    )
  }

  return (
    <Container
      id='config'
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {configVariables && !isLoading && !errorMsg && (
        <Row>
          <Col>
            <FormDivider title='Configuration Variables' />
            <ConfigTable variables={configVariables} />
            <Pagination />
          </Col>
        </Row>
      )}
    </Container>
  )
}

export default Config
