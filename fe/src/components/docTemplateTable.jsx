import React from 'react'
import Table from 'react-bootstrap/Table'
import { useNavigate } from 'react-router-dom'
import { DOC_TEMPLATES } from '@constants'
import {
  FaHashtag,
  FaFileAlt,
  FaBook,
  FaCode,
  FaFolder,
  FaServer,
  FaLink,
} from 'react-icons/fa'

const DocTemplateTable = ({ templates }) => {
  const navigate = useNavigate()

  return (
    <Table striped bordered hover size='sm' className='shadow'>
      <thead className='border border-warning'>
        <tr className='text-nowrap'>
          <th className='py-3 text-center'>
            <FaHashtag className='me-2' /> #
          </th>
          <th className='py-3'>
            <FaFileAlt className='me-2' /> Name
          </th>
          <th className='py-3'>
            <FaBook className='me-2' /> Subject
          </th>
          <th className='py-3'>
            <FaCode className='me-2' /> Data Template Class
          </th>
          <th className='py-3'>
            <FaFolder className='me-2' /> Template Path
          </th>
          <th className='py-3'>
            <FaServer className='me-2' /> Provider
          </th>
          <th className='py-3'>
            <FaLink className='me-2' /> API Endpoint
          </th>
        </tr>
      </thead>
      <tbody>
        {templates.map((template) => (
          <tr
            key={template.id}
            onClick={() => navigate(`${DOC_TEMPLATES}/${template.id}`)}
            style={{ cursor: 'pointer' }}
          >
            <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
              {template.id}
            </td>
            <td className='py-2 fw-bold'>{template.name}</td>
            <td className='py-2'>{template.subject}</td>
            <td className='py-2'>{template.dataTemplateClass || 'N/A'}</td>
            <td className='py-2'>{template.templatePath || 'N/A'}</td>
            <td className='py-2'>{template.templateProvider || 'N/A'}</td>
            <td className='py-2'>
              {template.templateProviderApiEndpoint || 'N/A'}
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  )
}

export default DocTemplateTable
