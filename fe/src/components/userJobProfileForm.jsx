import React, { useState, useEffect, useContext, useRef } from 'react'
import { Row, Col, FloatingLabel, Form, Button, Alert } from 'react-bootstrap'
import { postData } from '@utils/api' // Import postData function
import FormDivider from './formDivider'
import { SERVER_JOB_PROFILES } from '@endpoints'
import { EMPTY_USER_PROFILE } from '@constants'
import { putData } from '@utils/api'
import FormCompanyAutocomplete from './formCompanyAutocomplete'
import FormSelectBranch from './formSelectBranch'
import FormSelectContract from './formSelectContract'
import FormSelectBaseSalary from './formSelectBaseSalary'
import ModalAlert from './modalAlert'
import { ProfileContext } from '@pages/profile'

const UserJobProfileForm = () => {
  const { userData, setUserData } = useContext(ProfileContext)
  const { isEditable, setIsEditable } = useContext(ProfileContext)

  //
  // Save the original user data to return to the original state if the user cancels the edit
  const [originalUserData] = useState(userData)

  // [Level, Message]
  const [modalAlert, setModalAlert] = useState(['', ''])

  const [errorMsg, setErrorMsg] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const setSelectedCompany = (fieldName, company) => {
    fieldName === null // Field name is not used in this case because there is only one field

    //setCompanyId(company.id)

    setUserData((prevUserData) => {
      const updatedUserData = {
        ...prevUserData,
        jobProfile: {
          ...prevUserData.jobProfile,
          company: {
            id: company.id,
            description: company.description,
            sector: {
              id: company.sector.id,
              description: company.sector.description,
            },
          },
          // Reset the other fields when the company changes
          branch: {
            id: 0,
            description: 'Select...',
          },
          contract: {
            id: 0,
            level: 'Select...',
          },
          baseSalary: {
            id: 0,
            baseSalary: 'Select...',
          },
          hoursPerWeek: 0,
          jobProfileType: '',
          startDate: null,
          endDate: null,
          jobDescription: 'N/A',
        },
      }
      return updatedUserData
    })
  }

  const setSelectedBranch = (branchSelection) => {
    setUserData((prevUserData) => {
      const updatedUserData = {
        ...prevUserData,
        jobProfile: {
          ...prevUserData.jobProfile,
          branch: {
            id: branchSelection.id,
            description: branchSelection.description,
          },
        },
      }
      return updatedUserData
    })
  }

  const setSelectedContract = (contractSelection) => {
    setUserData((prevUserData) => {
      const updatedUserData = {
        ...prevUserData,
        jobProfile: {
          ...prevUserData.jobProfile,
          contract: contractSelection,
        },
      }
      return updatedUserData
    })
  }

  const setSelectedBaseSalary = (baseSalarySelection) => {
    setUserData((prevUserData) => {
      const updatedUserData = {
        ...prevUserData,
        jobProfile: {
          ...prevUserData.jobProfile,
          baseSalary: {
            id: baseSalarySelection.id,
            baseSalary: baseSalarySelection.baseSalary,
          },
        },
      }
      return updatedUserData
    })
  }

  const handleChange = (e) => {
    let { name, value } = e.target
    console.log('Name: ', name)
    console.log('Value: ', value)

    //
    // This is needed to modify non dropdown fields in the form
    //

    // Convert hoursPerWeek to a number
    if (name === 'hoursPerWeek') {
      value = Number(value)
    }

    // Set the new value in the form data
    setUserData((prevUserData) => {
      const updatedUserData = {
        ...prevUserData,
        jobProfile: {
          ...prevUserData.jobProfile,
          [name]: value,
        },
      }
      return updatedUserData
    })
  }

  useEffect(() => {
    console.log('------- UserJobProfileForm -------')
    console.log('userData: ', userData)
    console.log('------- UserJobProfileForm -------')
  }, [userData])

  return (
    <>
      <Row>
        <Col xs={12} sm={6}>
          <FormDivider title='Company' />
          <FormCompanyAutocomplete
            setSelectedCompany={setSelectedCompany}
            fieldName='companyId'
            placeholder='Company'
            jobProfile={userData.jobProfile}
            required
            disabled={!isEditable}
          />
        </Col>

        <Col xs={12} sm={6}>
          <FormDivider title='Branch' />
          <FormSelectBranch
            jobProfile={userData.jobProfile}
            setSelectedBranch={setSelectedBranch}
            disabled={!isEditable}
          />
        </Col>
      </Row>
      <Row>
        <Col xs={12} md={6}>
          <FormDivider title='Contract' />
          <FormSelectContract
            jobProfile={userData.jobProfile}
            setSelectedContract={setSelectedContract}
            disabled={!isEditable}
          />
        </Col>

        <Col xs={12} md={6}>
          <FormDivider title='Base Salary' />
          <FormSelectBaseSalary
            jobProfile={userData.jobProfile}
            setSelectedBaseSalary={setSelectedBaseSalary}
            disabled={!isEditable}
          />
        </Col>
      </Row>{' '}
      <FormDivider title='Other info' />
      <Row>
        <Col xs={4}>
          <FloatingLabel label='Hours Per Week' className='mb-3'>
            <Form.Control
              type='number'
              name='hoursPerWeek'
              value={userData.jobProfile.hoursPerWeek || ''}
              onChange={(e) => handleChange(e)}
              required
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col xs={4}>
          <FloatingLabel label='Job Profile Type' className='mb-3'>
            <Form.Select
              name='jobProfileType'
              value={userData.jobProfile.jobProfileType || ''}
              onChange={(e) => handleChange(e)}
              disabled={!isEditable}
            >
              <option value=''>Select...</option>
              <option value='FULL_TIME'>Full-time employment</option>
              <option value='PART_TIME'>Part-time employment</option>
              <option value='FIXED_TERM'>Fixed-term contract</option>
              <option value='APPRENTICESHIP'>Apprenticeship program</option>
              <option value='INTERNSHIP'>Internship position</option>
              <option value='REMOTE'>Remote work</option>
              <option value='FREELANCE'>Freelance work</option>
              <option value='TEMPORARY'>Temporary employment</option>
              <option value='SEASONAL'>Seasonal work</option>
              <option value='ZERO_HOURS_CONTRACT'>Zero-hours contract</option>
              <option value='VOLUNTEER'>Volunteer work</option>
              <option value='CONTRACTOR'>Contractor position</option>
              <option value='OTHER'>Other type of employment</option>
            </Form.Select>
          </FloatingLabel>
        </Col>
        <Col xs={12}>
          <FloatingLabel label='Job Description' className='mb-3'>
            <Form.Control
              type='text'
              name='jobDescription'
              value={userData.jobProfile.jobDescription || ''}
              onChange={(e) => handleChange(e)}
              style={{ height: '100px' }}
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
      </Row>
      <Row>
        <Col xs={12} sm={6}>
          <FloatingLabel label='Start Date' className='mb-3'>
            <Form.Control
              type='date'
              name='startDate'
              value={userData.jobProfile.startDate || ''}
              onChange={(e) => handleChange(e)}
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
        <Col xs={12} sm={6}>
          <FloatingLabel label='End Date' className='mb-3'>
            <Form.Control
              type='date'
              name='endDate'
              value={userData.jobProfile.endDate || ''}
              onChange={(e) => handleChange(e)}
              disabled={!isEditable}
            />
          </FloatingLabel>
        </Col>
      </Row>
    </>
  )
}
export default UserJobProfileForm
