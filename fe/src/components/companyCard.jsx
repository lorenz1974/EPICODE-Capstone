import React, { useEffect } from 'react'
import { useState, setState } from 'react'
import { Card, Row, Col, Table } from 'react-bootstrap'
import FormDivider from './formDivider'
import BrancesTable from './brancesTable'
import { GiFactory } from 'react-icons/gi'
import BranchEmployees from './branchEmployees'
import {
  FaMapMarkerAlt,
  FaCity,
  FaMap,
  FaEnvelope,
  FaPhone,
  FaStickyNote,
  FaBuilding,
  FaInfoCircle,
  FaMapSigns,
  FaUsers,
} from 'react-icons/fa'

const CompanyCard = ({
  id,
  description,
  piva,
  address,
  city,
  province,
  cap,
  phone,
  mail,
  notes,
  branches,
}) => {
  const [branchId, setBranchId] = useState(null)

  useEffect(() => {
    setBranchId(branches[0].id)
  }, [])

  return (
    <Card className='d-flex flex-column h-100 shadow'>
      <Card.Body className='d-flex flex-column justify-content-between'>
        <Card.Title className='p-3 fs-5 border border-light bg-warning'>
          <span className='fw-bold'>
            {' '}
            <GiFactory /> {description} [id: {id}]
          </span>{' '}
          - <span style={{ fontSize: '0.8em' }}>P.IVA : {piva || 'N/A'}</span>
        </Card.Title>
        <Row xs={1} md={2} lg={3} className='g-3'>
          <Col className='border border-0 border-end'>
            <FormDivider
              title={
                <>
                  <FaInfoCircle className='me-2' /> Informazioni
                </>
              }
              className='bg-light'
            />
            <Table>
              <tbody>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaMapMarkerAlt className='me-2' /> Address:
                  </th>
                  <td>{address || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaCity className='me-2' /> City:
                  </th>
                  <td>{city || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaMap className='me-2' /> Province:
                  </th>
                  <td>{province || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaBuilding className='me-2' /> CAP:
                  </th>
                  <td>{cap || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaPhone className='me-2' /> Phone:
                  </th>
                  <td>{phone || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaEnvelope className='me-2' /> Mail:
                  </th>
                  <td>{mail || 'N/A'}</td>
                </tr>
                <tr>
                  <th className=' text-nowrap fw-bold'>
                    <FaStickyNote className='me-2' /> Notes:
                  </th>
                  <td>{notes || 'N/A'}</td>
                </tr>
              </tbody>
            </Table>
          </Col>
          <Col className='border border-0 border-end'>
            <FormDivider
              title={
                <>
                  <FaMapSigns className='me-2' /> Sedi
                </>
              }
              className='bg-light'
            />
            {branches && branches.length > 0 ? (
              <BrancesTable
                branches={branches}
                branchId={branchId}
                setBranchId={setBranchId}
              />
            ) : (
              <p>No branches available</p>
            )}
          </Col>
          <Col className='border border-0 border-end'>
            <FormDivider
              title={
                <>
                  <FaUsers className='me-2' /> Dipendenti
                </>
              }
              className='bg-light'
            />
            <BranchEmployees branchId={branchId} />
          </Col>
        </Row>
      </Card.Body>
    </Card>
  )
}

export default CompanyCard
