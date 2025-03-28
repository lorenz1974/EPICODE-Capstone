import React from 'react'
import { useContext } from 'react'
import { Row, Col, Button } from 'react-bootstrap'
import { Context } from '../App'

const Pagination = () => {
  const { pagination, setPagination } = useContext(Context)
  const { currentPage, totalPages, pageSize, totalElements } = pagination

  return (
    <Row className='mt-4'>
      <Col className='d-flex justify-content-between'>
        <Button
          variant='secondary'
          onClick={() =>
            setPagination({ currentPage: Math.max(0, currentPage - 1) })
          }
          disabled={currentPage === 0}
        >
          Previous
        </Button>
        <span>
          Elements {Math.min(pageSize, totalElements)} of {totalElements} / Page{' '}
          {currentPage + 1} of {totalPages}
        </span>
        <Button
          variant='secondary'
          onClick={() =>
            setPagination({
              currentPage: Math.min(totalPages - 1, currentPage + 1),
            })
          }
          disabled={currentPage === totalPages - 1}
        >
          Next
        </Button>
      </Col>
    </Row>
  )
}
export default Pagination
