import React from 'react'
import { Card } from 'react-bootstrap'
import {
  FaFileAlt,
  FaCheckCircle,
  FaExclamationCircle,
  FaTimesCircle,
} from 'react-icons/fa'

const STATUS_STYLES = {
  CREATED: { background: '#d0ebff', icon: <FaFileAlt /> },
  SENT: { background: '#c3fae8', icon: <FaFileAlt /> },
  SIGNED: { background: '#d3f9d8', icon: <FaCheckCircle /> },
  FAILED: { background: '#ffe3e3', icon: <FaTimesCircle /> },
  ERROR: { background: '#ffe3e3', icon: <FaExclamationCircle /> },
  ACTIVE: { background: '#e7f5ff', icon: <FaFileAlt /> },
  WAITING: { background: '#fff3bf', icon: <FaFileAlt /> },
  CANCELED: { background: '#ffe8cc', icon: <FaTimesCircle /> },
  EXPIRED: { background: '#ffe3e3', icon: <FaExclamationCircle /> },
  PENDING: { background: '#fff3bf', icon: <FaFileAlt /> },
  DELETED: { background: '#ffe8cc', icon: <FaTimesCircle /> },
  REJECTED: { background: '#ffe8cc', icon: <FaTimesCircle /> },
  COMPLETED: { background: '#d3f9d8', icon: <FaCheckCircle /> },
  ANOMALY: { background: '#ffe8cc', icon: <FaExclamationCircle /> },
}

const SignRequestStatusCard = ({ status, statusArray }) => {
  const style = STATUS_STYLES[status] || {
    background: '#f8f9fa',
    icon: <FaFileAlt />,
  }
  const count = statusArray.find((item) => item[status])?.[status] || 0

  return (
    <Card
      style={{ background: style.background, borderRadius: '10px' }}
      className='text-center shadow'
    >
      <Card.Body>
        <div
          className='d-flex justify-content-center align-items-center mb-3'
          style={{ fontSize: '2rem' }}
        >
          {style.icon}
        </div>
        <Card.Title className='mb-2'>{status}</Card.Title>
        <Card.Text>
          <strong>{count}</strong> Requests
        </Card.Text>
      </Card.Body>
    </Card>
  )
}

export default SignRequestStatusCard
