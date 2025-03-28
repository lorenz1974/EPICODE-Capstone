import React from 'react'
import Table from 'react-bootstrap/Table'
import {
  FaCheckCircle,
  FaSpinner,
  FaClipboardCheck,
  FaFileAlt,
  FaExclamationCircle,
  FaTimesCircle,
  FaCalendarAlt,
  FaListAlt,
  FaClipboardList,
  FaEnvelope,
  FaIdBadge,
  FaFileSignature,
} from 'react-icons/fa'
import { formatDate, formatIsoDate } from '@utils/functions'

const STATUS_STYLES = {
  STARTED: { background: '#d0ebff', icon: <FaSpinner /> },
  IN_PROGRESS: { background: '#fff3bf', icon: <FaSpinner /> },
  COMPLETED: { background: '#d3f9d8', icon: <FaCheckCircle /> },
}

const SIGN_REQUEST_STATUS_STYLES = {
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

const SignRequestChronologiesTable = ({ chronologies }) => {
  return (
    <>
      <Table striped bordered hover size='sm' className='shadow'>
        <thead className='border border-warning'>
          <tr className='text-nowrap'>
            <th className='py-3 text-center'>#</th>
            <th className='py-3'>
              <FaCalendarAlt className='me-2' /> Created At
            </th>
            <th className='py-3'>
              <FaListAlt className='me-2' /> Event Type
            </th>
            <th className='py-3'>
              <FaClipboardList className='me-2' /> Status
            </th>
            <th className='py-3'>
              <FaEnvelope className='me-2' /> Message
            </th>
            <th className='py-3 text-center'>
              <FaIdBadge className='me-2' /> Sign Request ID
            </th>
            <th className='py-3'>
              <FaFileSignature className='me-2' /> Sign Request Status
            </th>
          </tr>
        </thead>
        <tbody>
          {chronologies.map((chronology) => {
            const statusStyle = STATUS_STYLES[chronology.status] || {
              background: '#f8f9fa',
              icon: null,
            }
            const signRequestStatusStyle = SIGN_REQUEST_STATUS_STYLES[
              chronology.signRequest.status
            ] || {
              background: '#f8f9fa',
              icon: null,
            }
            return (
              <tr key={chronology.id}>
                <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
                  {chronology.id}
                </td>
                <td className='py-2 text-center'>
                  {formatIsoDate(chronology.createdAt)}
                </td>
                <td className='py-2'>{chronology.eventType}</td>
                <td
                  className='py-2 fw-bold'
                  style={{ background: statusStyle.background }}
                >
                  <div className='d-flex align-items-center justify-content-start'>
                    {statusStyle.icon && (
                      <span className='me-2'>{statusStyle.icon}</span>
                    )}
                    {chronology.status}
                  </div>
                </td>
                <td className='py-2 fst-italic'>{chronology.message}</td>
                <td className='py-2 text-center'>
                  {chronology.signRequest.id}
                </td>
                <td
                  className='py-2 fw-bold text-center'
                  style={{ background: signRequestStatusStyle.background }}
                >
                  <div className='d-flex align-items-center justify-content-center'>
                    {signRequestStatusStyle.icon && (
                      <span className='me-2'>
                        {signRequestStatusStyle.icon}
                      </span>
                    )}
                    {chronology.signRequest.status}
                  </div>
                </td>
              </tr>
            )
          })}
        </tbody>
      </Table>
    </>
  )
}

export default SignRequestChronologiesTable
