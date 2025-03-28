import React, { useState, useEffect, useRef, useContext } from 'react'
import { isAuthenticated } from '@utils/auth'
import { Form, FloatingLabel, Spinner } from 'react-bootstrap'
import { getData } from '@utils/api'
import { SERVER_COMPANIES } from '@endpoints'
import { ProfileContext } from '@pages/profile'

const FormSelectBranch = ({ setSelectedBranch, disabled }) => {
  const { userData, setUserData } = useContext(ProfileContext)

  // To avoid unnecessary fetches and re-renders
  const prevCompanyId = useRef('')

  const [companyId, setCompanyId] = useState(userData.jobProfile.company.id)

  const [branches, setBranches] = useState([])

  const [formData, setFormData] = useState({
    id: userData.jobProfile.branch.id,
    description: userData.jobProfile.branch.description,
  })

  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')

  const handleChange = (e) => {
    const value = Number(e.target.value)
    const description = e.target.options[e.target.selectedIndex].label

    setFormData({
      ...formData,
      id: value,
      description: description,
    })

    setSelectedBranch({ id: value, description: description })
  }

  useEffect(() => {
    // Aggiorna companyId quando userData.jobProfile cambia
    setCompanyId(userData.jobProfile.company.id)

    const fetchBranches = async () => {
      setIsLoading(true)
      setErrorMsg('')

      try {
        const data = await getData(
          `${SERVER_COMPANIES}/${userData.jobProfile.company.id}`
        )
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (!data.branches || data.branches.length === 0) {
            setErrorMsg('No branches available')
          }
          setBranches(data.branches)
          return data.branches || []
        }
        return data.branches || []
      } catch (error) {
        setErrorMsg('Server fetching error: ' + error.message)
        return []
      } finally {
        setIsLoading(false)
      }
    }

    if (
      isAuthenticated() &&
      userData.jobProfile.company.id !== prevCompanyId.current
    ) {
      prevCompanyId.current = userData.jobProfile.company.id
      fetchBranches()
    }
  }, [userData.jobProfile])

  //.
  // It will set the branch description when the userData changes
  // This is needed when the user is editing the profile and the branch description is already set
  //
  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      id: userData.jobProfile.branch.id,
      description: userData.jobProfile.branch.description,
    }))

    console.log('**************************')
    console.log('FormSelectBranch - userData: ', userData)
    console.log('**************************')
  }, [userData])

  return (
    <div className='m-0 p-0 position-relative'>
      <FloatingLabel label='Branch' className='mb-3'>
        <Form.Select
          title='Branch'
          name='branchId'
          value={formData.id}
          onChange={(e) => handleChange(e)}
          required
          disabled={disabled}
        >
          <option value='a'>Select...</option>
          {branches.map((branch) => (
            <option
              key={branch.id}
              value={branch.id}
              description={branch.description}
            >
              {branch.description}
            </option>
          ))}
        </Form.Select>
      </FloatingLabel>
      {isLoading && <Spinner animation='border' size='sm' />}

      {errorMsg && (
        <div
          className='text-danger position-absolute'
          style={{ fontSize: '.8em', top: '18px', right: '15px' }}
        >
          {errorMsg}
        </div>
      )}
    </div>
  )
}
export default FormSelectBranch
