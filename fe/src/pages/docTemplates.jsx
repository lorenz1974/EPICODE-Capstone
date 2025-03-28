import React, { useState, useEffect, useContext } from 'react'
import { Container, Alert, Row, Col } from 'react-bootstrap'
import { SERVER_DOC_TEMPLATES } from '@endpoints'
import { DOC_TEMPLATES } from '@constants'
import { getData } from '@utils/api'
import DocTemplateTable from '@components/DocTemplateTable'
import Pagination from '@components/pagination'
import { Context } from '../App'
import FormDivider from '@components/formDivider'
import { useNavigate, useParams } from 'react-router-dom'
import { LOGOUT } from '@constants'
import CustomSpinner from '@components/customSpinner'
import FormDocTemplate from '@components/FormDocTemplate'

const DocTemplates = () => {
  const navigate = useNavigate()
  const { id } = useParams() // Get the ID from the URL
  const { pagination, setPagination } = useContext(Context)
  const { q, setQ } = useContext(Context)
  const [docTemplates, setDocTemplates] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    const fetchDocTemplates = async () => {
      setIsLoading(true)
      setErrorMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: 25,
          sortBy: 'name',
          direction: 'ASC',
          alsoDeleted: false,
          q: q,
        }
        const data = await getData(SERVER_DOC_TEMPLATES, params)
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setDocTemplates(data._embedded.docTemplateList)
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
      fetchDocTemplates()
    }
  }, [pagination.currentPage, q, id])

  if (id) {
    return (
      <Container>
        <FormDocTemplate id={id} onClose={() => navigate(DOC_TEMPLATES)} />
      </Container>
    )
  }

  return (
    <Container
      id='doc-templates'
      fluid
      className='d-flex justify-content-center align-items-start'
    >
      {isLoading && <CustomSpinner />}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {docTemplates && !isLoading && !errorMsg && (
        <Row>
          <Col>
            <FormDivider title='Document Templates' />
            <DocTemplateTable templates={docTemplates} />
            <Pagination />
          </Col>
        </Row>
      )}
    </Container>
  )
}

export default DocTemplates
