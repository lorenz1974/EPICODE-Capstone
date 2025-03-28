import React from 'react'
import Table from 'react-bootstrap/Table'
import { useNavigate } from 'react-router-dom'
import {
  FaHashtag,
  FaFileAlt,
  FaCode,
  FaFolder,
  FaCalendarAlt,
  FaFileCode,
  FaTrash,
  FaKey,
  FaLink,
} from 'react-icons/fa'
import { formatIsoDate } from '@utils/functions'
import { saveAs } from 'file-saver'
import { FILES } from '@constants'
import { Button } from 'react-bootstrap'
import axios from 'axios'
import { getData } from '@utils/api'

const FilesTable = ({ files }) => {
  const navigate = useNavigate()

  const handleDownload = async (fileId, filename) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/files/${fileId}/download`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${
              localStorage.getItem('token') || sessionStorage.getItem('token')
            }`,
          },
        }
      )

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
    } catch (error) {
      console.error('Download error:', error)
    }
  }

  return (
    <Table striped bordered hover size='sm' className='shadow'>
      <thead className='border border-warning'>
        <tr className='text-nowrap'>
          <th className='py-3 text-center'>
            <FaHashtag className='me-2' />
          </th>
          <th className='py-3'>
            <FaFileAlt className='me-2' /> Filename
          </th>
          <th className='py-3'>
            <FaCode className='me-2 text-center' /> Extension
          </th>
          <th className='py-3'>
            <FaFileCode className='me-2' /> MIME Type
          </th>
          <th className='py-3'>
            <FaLink className='me-2 text-nowrap' /> Full File Path
          </th>
          <th className='py-3'>
            <FaKey className='me-2' /> Father Type
          </th>
          <th className='py-3'>
            <FaHashtag className='me-2 text-center' /> Father ID
          </th>
          <th className='py-3'>
            <FaTrash className='me-2 text-center' /> Deleted
          </th>
          <th className='py-3'>
            <FaCalendarAlt className='me-2' /> Created At
          </th>
          <th> </th>
          <th> </th>
        </tr>
      </thead>
      <tbody>
        {files.map((file) => (
          <tr
            key={file.id}
            onClick={() => navigate(`${FILES}/${file.id}`)}
            style={{ cursor: 'pointer' }}
          >
            <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
              {file.id}
            </td>
            <td className='py-2 fw-bold'>{file.originalFilename}</td>
            <td className='py-2 text-center'>{file.extension}</td>
            <td className='py-2'>{file.mimeType}</td>
            <td className='py-2'>{file.fullFilePath}</td>
            <td className='py-2'>{file.fatherType}</td>
            <td className='py-2 text-center'>{file.fatherId}</td>
            <td className='py-2 text-center'>{file.deleted ? 'Yes' : 'No'}</td>
            <td className='py-2'>{formatIsoDate(file.createdAt)}</td>
            <td>
              <Button
                size='sm'
                onClick={(e) => {
                  e.stopPropagation()
                  handleDownload(file.id, file.originalFilename)
                }}
              >
                Download
              </Button>
            </td>
            <td>
              <Button size='sm' variant='danger'>
                Delete
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  )
}

export default FilesTable
