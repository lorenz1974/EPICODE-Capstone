import React, { useState, useEffect, useRef, useContext } from 'react'
import { isAuthenticated } from '@utils/auth'
import { Form, FloatingLabel, Spinner } from 'react-bootstrap'
import { getData } from '@utils/api'
import { SERVER_CONTRACTS } from '@endpoints'
import { ProfileContext } from '@pages/profile'

const FormSelectContract = ({ jobProfile, setSelectedContract, disabled }) => {
  const { userData, setUserData } = useContext(ProfileContext)

  // To avoid unnecessary fetches and re-renders
  const prevCompanyId = useRef('')

  const [contractId, setContractId] = useState(jobProfile.contract.id || 0)

  const [contracts, setContracts] = useState([])

  const [formData, setFormData] = useState({
    id: jobProfile.contract.id,
    description: jobProfile.contract.description,
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

    //
    // It will find the selected contract in the contracts array and set it as the selected contract
    // that's because also the base salary is needed by the father component
    //
    const selectedContract = contracts.find(
      (contract) => contract.id === Number(value) // Number(value) is needed because value is a string
    )
    setSelectedContract(selectedContract)
  }

  useEffect(() => {
    // Aggiorna contractId quando jobProfile cambia
    setContractId(jobProfile.contract.id || 0)

    const fetchContracts = async () => {
      setIsLoading(true)
      setErrorMsg('')

      try {
        const params = {
          page: 0,
          size: 1000, // All contracts by sector are needed in this case
          sortBy: 'level',
          direction: 'ASC',
        }
        const data = await getData(
          `${SERVER_CONTRACTS}/sectors/${jobProfile.company.sector.id}`,
          params
        )
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          if (data.page.totalElements !== 0) {
            setContracts(data._embedded.contractList)
          } else {
            setErrorMsg('No data found')
          }
        }
      } catch (error) {
        console.error('Error fetching contracts: ', error)
        if (error instanceof TypeError) {
          setErrorMsg('Contract not found')
        } else {
          setErrorMsg('Server fetching error: ' + error.message)
        }
        return []
      } finally {
        setIsLoading(false)
      }
    }

    if (isAuthenticated() && jobProfile.company.id !== prevCompanyId.current) {
      prevCompanyId.current = jobProfile.company.id
      fetchContracts()
    }
  }, [jobProfile])

  //
  // It will set the contract description when the userData changes
  // This is needed when the user is editing the profile and the contract description is already set
  //
  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      id: userData.jobProfile.contract.id,
      description: userData.jobProfile.contract.description,
    }))
  }, [userData])

  return (
    <div className='m-0 p-0 position-relative'>
      <FloatingLabel label='Contract' className='mb-3'>
        <Form.Select
          title='Contract'
          name='contractId'
          value={formData.id}
          onChange={(e) => handleChange(e)}
          required
          disabled={disabled}
          className='position-relative'
        >
          <option value='a'>Select...</option>
          {contracts.map((contract) => (
            <option
              key={contract.id}
              value={contract.id}
              description={contract.level}
            >
              {contract.level}
            </option>
          ))}
        </Form.Select>
        {isLoading && <Spinner animation='border' size='sm' />}

        {errorMsg && (
          <div
            className='text-danger position-absolute'
            style={{ fontSize: '.8em', top: '18px', right: '15px' }}
          >
            {errorMsg}
          </div>
        )}
      </FloatingLabel>
    </div>
  )
}
export default FormSelectContract
