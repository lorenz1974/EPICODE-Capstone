import { Col, Row } from 'react-bootstrap'

const FormDivider = ({ title, className }) => {
  return (
    <Row className='p-2 '>
      <Col className={className}>
        <h4>{title}</h4>
        <hr />
      </Col>
    </Row>
  )
}

export default FormDivider
