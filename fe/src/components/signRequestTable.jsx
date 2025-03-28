import React, { useEffect, useContext, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Table from 'react-bootstrap/Table'
import { formatIsoDate } from '@utils/functions'
import { ProfileContext } from '@pages/profile'
import { SIGN_REQUESTS, USERS } from '@constants'
import {
  FaFileAlt,
  FaCheckCircle,
  FaExclamationCircle,
  FaTimesCircle,
  FaUser,
  FaCalendarAlt,
  FaClipboard,
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

const SignRequestTable = ({ requests, signed }) => {
  const { userData } = useContext(ProfileContext) || {}
  const [signRequests, setSignRequests] = useState([])
  const navigate = useNavigate()

  useEffect(() => {
    if (requests) {
      setSignRequests(requests)
    } else {
      const userRequests = signed
        ? userData?.signRequestsSigned
        : userData?.signRequestsCreated
      const sortedRequests = (userRequests || []).sort((a, b) => a.id - b.id)
      setSignRequests(sortedRequests)
    }
  }, [requests, signed, userData])

  const hasSignedBy = signRequests.some((req) => req.signedByAppUser)
  const hasCreatedBy = signRequests.some((req) => req.createdByAppUser)

  return (
    <Table striped bordered hover size='sm' className='shadow'>
      <thead className='border border-warning'>
        <tr className='text-nowrap'>
          <th className='py-3 text-center'>
            <FaClipboard className='me-2' />#
          </th>
          <th className='py-3'>
            <FaFileAlt className='me-2' />
            Doc Template
          </th>
          <th className='py-3'>
            <FaClipboard className='me-2' />
            Subject
          </th>
          {hasSignedBy && (
            <th className='py-3'>
              <FaUser className='me-2' />
              Signed By
            </th>
          )}
          {hasCreatedBy && (
            <th className='py-3'>
              <FaUser className='me-2' />
              Created By
            </th>
          )}
          <th className='py-3'>
            <FaExclamationCircle className='me-2' />
            Status
          </th>
          <th className='py-3 d-none d-md-block'>
            <FaCalendarAlt className='me-2' />
            Created At
          </th>
          <th className='py-3'>
            <FaCalendarAlt className='me-2' />
            Updated At
          </th>
        </tr>
      </thead>
      <tbody>
        {signRequests.map((request) => {
          const statusStyle = STATUS_STYLES[request.status] || {
            background: '#f8f9fa',
            icon: <FaFileAlt />,
          }
          return (
            <tr
              key={request.id}
              onClick={() => navigate(`${SIGN_REQUESTS}/${request.id}`)}
              style={{ cursor: 'pointer' }}
            >
              <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
                {request.id}
              </td>
              <td className='py-2'>{request.docTemplate?.name || '...'}</td>
              <td className='py-2'>{request.subject || '...'}</td>
              {hasSignedBy && (
                <td className='py-2'>
                  {request.signedByAppUser ? (
                    <span
                      style={{ cursor: 'pointer' }}
                      onClick={(e) => {
                        e.stopPropagation()
                        navigate(`${USERS}/${request.signedByAppUser.id}`)
                      }}
                    >
                      {`${request.signedByAppUser.name} ${request.signedByAppUser.surname}`}
                    </span>
                  ) : (
                    'N/A'
                  )}
                </td>
              )}
              {hasCreatedBy && (
                <td className='py-2'>
                  {request.createdByAppUser ? (
                    <span
                      style={{ cursor: 'pointer' }}
                      onClick={(e) => {
                        e.stopPropagation()
                        navigate(`${USERS}/${request.createdByAppUser.id}`)
                      }}
                    >
                      {`${request.createdByAppUser.name} ${request.createdByAppUser.surname}`}
                    </span>
                  ) : (
                    'N/A'
                  )}
                </td>
              )}
              <td
                className='py-2'
                style={{ background: statusStyle.background }}
              >
                <div className='d-flex align-items-center justify-content-start'>
                  <span className='me-2'>{statusStyle.icon}</span>
                  {request.status || 'N/A'}
                </div>
              </td>
              <td className='d-none d-md-block py-2'>
                {formatIsoDate(request.createdAt)}
              </td>
              <td className='py-2'>{formatIsoDate(request.updatedAt)}</td>
            </tr>
          )
        })}
      </tbody>
    </Table>
  )
}

export default SignRequestTable
