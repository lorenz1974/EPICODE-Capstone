import React, { useState, useEffect, useContext } from 'react'
import { Container, Spinner, Alert, Row, Col } from 'react-bootstrap'
import { SERVER_FILES } from '@endpoints'
import { getData } from '@utils/api'
import FilesTable from '@components/FilesTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate } from 'react-router-dom'
import { LOGOUT } from '@constants'

const Files = () => {
  const navigate = useNavigate()
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [files, setFiles] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchFiles = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 25,
          sortBy: 'originalFilename',
          direction: 'ASC',
          q: q,
        }
        const data = await getData(SERVER_FILES, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setFiles(data._embedded.fileEntityList)
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

    fetchFiles()
  }, [pagination.currentPage, q])

  return (
    <Container
      id='files'
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <Spinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {files && !isLoading && !errorMsg && (
        <Row>
          <Col>
            <FormDivider title='Files' />
            <FilesTable files={files} />
            <Pagination />
          </Col>
        </Row>
      )}
    </Container>
  )
}

export default Files
