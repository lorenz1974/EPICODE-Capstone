import React from 'react'
import { Container, Alert } from 'react-bootstrap'

const Page404 = () => {
  return (
    <Container className='mt-5'>
      <Alert variant='danger'>
        <h1>404 - Page Not Found</h1>
        <p>The page you are looking for does not exist.</p>
      </Alert>
    </Container>
  )
}

export default Page404
