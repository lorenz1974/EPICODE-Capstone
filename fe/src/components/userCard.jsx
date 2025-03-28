import React from 'react'
import { Card, Row, Col, Button } from 'react-bootstrap'
import { GiFactory } from 'react-icons/gi'
import { FaRegBuilding } from 'react-icons/fa'
import { LuMapPin } from 'react-icons/lu'
import { FaUserEdit } from 'react-icons/fa'
import { LiaFileSignatureSolid } from 'react-icons/lia'
import Avatar from '@components/Avatar'
import { useNavigate } from 'react-router-dom'
import { COMPANIES } from '@constants'

const UserCard = (user) => {
  const navigate = useNavigate()

  console.log('UserCard - user: ', user)

  return (
    <Card className='d-flex flex-column h-100 shadow'>
      <Card.Body className='d-flex flex-column justify-content-between'>
        {/* Contenuto principale */}
        <div>
          <Avatar
            sex={user.sex}
            id={user.id}
            className='rounded-circle p-4 w-100'
            onlySource={false}
          />
          <Card.Title className='fs-5 text-center'>
            {user.surname} {user.name}
          </Card.Title>

          <Row className='d-flex flex-column text-center mb-3'>
            <Col className='fw-bold mb-1'>{user.fiscalcode}</Col>
            <Col style={{ fontSize: '.7em' }}>
              Nat* il {user.birthDate} a {user.birthPlace} ({user.birthProvince}
              )
            </Col>
          </Row>

          <hr />

          <Row className='justify-content-start align-content-center m-auto'>
            <Col xs={1} className='align-content-start m-0 p-0 flex-shrink-1'>
              <GiFactory />
            </Col>
            <Col
              onClick={() =>
                navigate(COMPANIES + '/' + user.jobProfile.company.id)
              }
              className='fw-bold align-content-start cursor-pointer'
              style={{ fontSize: '.8em', textAlign: 'left' }}
            >
              {user.jobProfile?.company?.description}
            </Col>
          </Row>

          <Row className='justify-content-start align-content-center m-auto'>
            <Col xs={1} className='align-content-start m-0 p-0'>
              <FaRegBuilding />
            </Col>
            <Col
              className='align-content-start'
              style={{ fontSize: '.8em', textAlign: 'left' }}
            >
              {user.jobProfile?.branch?.description}
            </Col>
          </Row>

          <Row className='justify-content-start align-content-center m-auto'>
            <Col xs={1} className='align-content-start m-0 p-0'>
              <LuMapPin />
            </Col>
            <Col
              className='align-content-start'
              style={{ fontSize: '.8em', textAlign: 'left' }}
            >
              {user.jobProfile.branch.city}
            </Col>
          </Row>
        </div>

        {/* Footer ancorato in fondo */}
        <div className='mt-3'>
          <hr />
          <div className='d-flex justify-content-around'>
            <Button
              className='px-5 py-3 px-sm-4 py-sm-2'
              variant='warning'
              onClick={() => navigate(`/users/${user.id}`)}
            >
              <FaUserEdit />
            </Button>
            <Button className='px-5 py-3 px-sm-4 py-sm-2' variant='warning'>
              <LiaFileSignatureSolid />
            </Button>
          </div>
        </div>
      </Card.Body>
    </Card>
  )
}

export default UserCard
