import React, { useEffect, useContext } from 'react'
import { Row, Col, FloatingLabel, Form } from 'react-bootstrap'
import FormCityAutocomplete from './formCityAutocomplete'
import FormDivider from './formDivider'
import { getData } from '@utils/api'

import { ProfileContext } from '@pages/profile'

const UserDetailForm = ({}) => {
  const { userData, setUserData } = useContext(ProfileContext)
  const { isEditable, setIsEditable } = useContext(ProfileContext)

  const handleChange = (e) => {
    let { name, value } = e.target

    if (name === 'fiscalcode') {
      value = value.toUpperCase()
    }

    if ((name === 'cellphone' || name === 'phone') && value) {
      // If the phone number does not start with +39, add it
      // this is necessary for the internationalization of the phone number
      if (!value.startsWith('+39')) {
        value = '+39' + value
      }
      // ad a space at the 4th and 7th position
      // value = value.replace(/(\+\d{2})(\d{3})(\d{4})/, '$1 $2 $3')
    }
    setUserData({ ...userData, [name]: value })
  }

  const handleOnBlur = (e) => {
    let { name, value } = e.target
  }

  const setCitySelected = (fieldName, citySelected) => {
    if (fieldName === 'birthPlace') {
      setUserData((prev) => ({
        ...prev,
        birthPlace: citySelected.cityState,
        birthProvince: citySelected.province,
        nationality: citySelected.state,
      }))
    } else if (fieldName === 'livingCity') {
      setUserData((prev) => ({
        ...prev,
        livingCity: citySelected.cityState,
        livingProvince: citySelected.province,
        zipCode: citySelected.zipCode,
      }))
    }
  }

  useEffect(() => {
    console.log('------- UserDetailForm -------')
    console.log('userData: ', userData)
    console.log('------- UserDetailForm -------')
  }, [userData])

  return (
    <>
      <Row>
        <Col sm={4} md={3}>
          <FloatingLabel label='Username' className='mb-3' hidden={true}>
            <Form.Control
              type='text'
              placeholder='Username'
              name='username'
              value={userData.username}
              onChange={handleChange}
              required
              disabled={true}
            />
          </FloatingLabel>
        </Col>
      </Row>
      <Row>
        <Col xs={12} sm={6} md={5}>
          <FloatingLabel label='Name' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Name'
              name='name'
              value={userData.name}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>

        <Col xs={12} sm={6} md={5}>
          <FloatingLabel label='Surname' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Surname'
              name='surname'
              value={userData.surname}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col xs={5} md={2}>
          <FloatingLabel label='Sex' className='mb-3'>
            <Form.Select
              name='sex'
              value={userData.sex}
              onChange={handleChange}
              required
              disabled={!isEditable}
            >
              <option value=''>Select</option>
              <option value='M'>M</option>
              <option value='F'>F</option>
            </Form.Select>
          </FloatingLabel>
        </Col>
      </Row>
      <Row>
        <Col xs={12} sm={6} md={6}>
          <FloatingLabel label='Birth Date' className='mb-3'>
            <Form.Control
              type='date'
              name='birthDate'
              value={userData.birthDate}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col xs={12} sm={6} md={6}>
          <FormCityAutocomplete
            setCitySelected={setCitySelected}
            fieldName='birthPlace'
            placeholder='Birth Place'
            initialValue={userData.birthPlace}
            required={true}
            disabled={!isEditable}
          />
        </Col>
      </Row>
      <Row>
        <Col xs={12} sm={6} md={6}>
          <FloatingLabel label='Birth Province' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Birth Province'
              name='birthProvince'
              value={userData.birthProvince}
              onChange={handleChange}
              required
              disabled={true}
            />
          </FloatingLabel>
        </Col>
        <Col xs={12} sm={6}>
          <FloatingLabel label='Nationality' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Nationality'
              name='nationality'
              value={userData.nationality}
              onChange={handleChange}
              required
              disabled={true}
            />
          </FloatingLabel>
        </Col>
      </Row>

      <FormDivider title='Codice Fiscale' />

      <Row>
        <Col className='d-flex justify-content-center'>
          <FloatingLabel
            label='Fiscal Code'
            className='mb-3  border border-success rounded-3 fs-5'
          >
            <Form.Control
              className='fw-bold fs-2 h-100 text-success text-center'
              type='text'
              placeholder='Fiscal Code'
              name='fiscalcode'
              value={userData.fiscalcode}
              onChange={handleChange}
              required
              disabled={!isEditable}
              onBlur={(e) => handleOnBlur(e)}
            />
          </FloatingLabel>
        </Col>
      </Row>

      <FormDivider title='Residenza' />

      <Row>
        <Col md={12}>
          <FloatingLabel label='Address' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Address'
              name='address'
              value={userData.address}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col xs={12} sm={5}>
          <FormCityAutocomplete
            setCitySelected={setCitySelected}
            fieldName='livingCity'
            placeholder='Living City'
            initialValue={userData.livingCity}
            required={true}
            disabled={!isEditable}
          />
        </Col>
        <Col xs={12} sm={5}>
          <FloatingLabel label='Living Province' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Living Province'
              name='livingProvince'
              value={userData.livingProvince}
              onChange={handleChange}
              required
              disabled={true}
            />
          </FloatingLabel>
        </Col>
        <Col xs={6} sm={2}>
          <FloatingLabel label='ZIP Code' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='ZIP Code'
              name='zipCode'
              value={userData.zipCode}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
      </Row>

      <FormDivider title='Contatti' />

      <Row>
        <Col md={4}>
          <FloatingLabel label='Email' className='mb-3'>
            <Form.Control
              type='email'
              placeholder='Email'
              name='email'
              value={userData.email}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col md={4}>
          <FloatingLabel label='Cellphone' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Cellphone'
              name='cellphone'
              value={userData.cellphone}
              onChange={handleChange}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col md={4}>
          <FloatingLabel label='Phone' className='mb-3'>
            <Form.Control
              type='text'
              placeholder='Phone'
              name='phone'
              value={userData.phone}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
      </Row>

      <FormDivider title='Note' />

      <Row>
        <Col>
          <FloatingLabel label='Notes' className='mb-3'>
            <Form.Control
              as='textarea'
              placeholder='Notes'
              name='notes'
              value={userData.notes}
              onChange={handleChange}
              style={{ height: '100px' }}
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
      </Row>
    </>
  )
}
export default UserDetailForm
