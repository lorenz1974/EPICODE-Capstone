import React from 'react'
import Table from 'react-bootstrap/Table'
import {
  FaCheckCircle,
  FaExclamationCircle,
  FaSpinner,
  FaNetworkWired,
  FaDesktop,
  FaWindows,
  FaChrome,
  FaUser,
  FaCalendarAlt,
  FaEnvelope,
} from 'react-icons/fa'
import { formatIsoDate } from '../utils/functions'

const STATUS_STYLES = {
  FAILED: { background: '#ffe3e3', icon: <FaExclamationCircle /> },
  TRYING: { background: '#fff3bf', icon: <FaSpinner /> },
  SUCCESS: { background: '#d3f9d8', icon: <FaCheckCircle /> },
}

const LoginDataTable = ({ data }) => {
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
              <FaUser className='me-2' /> Username
            </th>
            <th className='py-3'>
              <FaNetworkWired className='me-2' /> IP Address
            </th>
            <th className='py-3'>
              <FaDesktop className='me-2' /> Device
            </th>
            <th className='py-3'>
              <FaWindows className='me-2' /> OS
            </th>
            <th className='py-3'>
              <FaChrome className='me-2' /> Browser
            </th>
            <th className='py-3'>
              <FaEnvelope className='me-2' /> Message
            </th>
            <th className='py-3'>Status</th>
          </tr>
        </thead>
        <tbody>
          {data.map((entry) => {
            const statusStyle = STATUS_STYLES[entry.status] || {
              background: '#f8f9fa',
              icon: null,
            }
            return (
              <tr key={entry.id}>
                <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
                  {entry.id}
                </td>
                <td className='py-2 fw-bold'>
                  {formatIsoDate(entry.createdAt)}
                </td>
                <td className='py-2 fw-bold'>{entry.username}</td>
                <td className='py-2'>{entry.ipAddress}</td>
                <td className='py-2'>{entry.device}</td>
                <td className='py-2'>{entry.os}</td>
                <td className='py-2'>{entry.browser}</td>
                <td className='py-2 fst-italic'>{entry.message}</td>
                <td
                  className='py-2 fw-bold'
                  style={{ background: statusStyle.background }}
                >
                  <div className='d-flex align-items-center justify-content-start'>
                    {statusStyle.icon && (
                      <span className='me-2'>{statusStyle.icon}</span>
                    )}
                    {entry.status}
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

export default LoginDataTable
