import React from 'react'
import Table from 'react-bootstrap/Table'
import { useNavigate } from 'react-router-dom'
import { CONFIG } from '@constants'

const ConfigTable = ({ variables }) => {
  const navigate = useNavigate()

  return (
    <Table striped bordered hover size='sm' className='shadow'>
      <thead className='border border-warning'>
        <tr className='text-nowrap'>
          <th className='py-3 text-center'>#</th>
          <th className='py-3'>Variable Name</th>
          <th className='py-3'>Value</th>
          <th className='py-3'>Type</th>
          <th className='py-3'>Description</th>
        </tr>
      </thead>
      <tbody>
        {variables.map((variable) => (
          <tr
            key={variable.id}
            onClick={() => navigate(`${CONFIG}/${variable.id}`)}
            style={{ cursor: 'pointer' }}
          >
            <td className='py-2 text-center' style={{ fontSize: '.8em' }}>
              {variable.id}
            </td>
            <td className='py-2 fw-bold'>{variable.variableName}</td>
            <td className='py-2 fst-italic'>
              {variable.value.replace(/\\\\/g, '\\')}
            </td>
            <td className='py-2'>{variable.type}</td>
            <td className='py-2'>{variable.description || ''}</td>
          </tr>
        ))}
      </tbody>
    </Table>
  )
}

export default ConfigTable
