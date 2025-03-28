import React, { useEffect, useState, useContext } from 'react'
import { Form, FloatingLabel } from 'react-bootstrap'
import { ProfileContext } from '@pages/profile'

const FormSelectBaseSalary = ({
  jobProfile,
  setSelectedBaseSalary,
  disabled,
}) => {
  const { userData, setUserData } = useContext(ProfileContext)

  const [formData, setFormData] = useState({
    id: jobProfile.baseSalary.id,
    baseSalary: jobProfile.baseSalary.baseSalary,
  })

  const handleChange = (e) => {
    const value = e.target.value
    const baseSalary = e.target.options[e.target.selectedIndex].label

    setFormData({
      ...formData,
      id: value,
      baseSalary: baseSalary,
    })

    //
    // It will find the selected base salary in the base salaries array and set it as the selected base salary
    // thats because also the base salary is needed by the father component
    //
    const selectedBaseSalary = (jobProfile.contract?.baseSalaries || []).find(
      (baseSalary) => baseSalary.id === Number(value) // Number(value) is needed because value is a string
    )
    setSelectedBaseSalary(selectedBaseSalary)
  }

  useEffect(() => {
    // It set base salary when the component is rendered with a base salary already selected
    // and the user does not touch it, the hook onChange will not be triggered
    // and when the form is submitted it generate an error because the base salary is not set
    //
  }, [jobProfile]) // <-- empty array means 'run once'

  //
  // It will set the contract description when the userData changes
  // This is needed when the user is editing the profile and the contract description is already set
  //
  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      id: userData.jobProfile.baseSalary.id,
      baseSalary: userData.jobProfile.baseSalary.baseSalary,
    }))
  }, [userData])

  return (
    <div className='m-0 p-0 position-relative'>
      <FloatingLabel label='Base Salary' className='mb-3'>
        <Form.Select
          name='baseSalaryId'
          value={formData.id}
          onChange={(e) => handleChange(e)}
          required
          disabled={disabled}
        >
          <option value='a'>Select...</option>
          {jobProfile.contract?.baseSalaries ? (
            (jobProfile.contract?.baseSalaries || []).map((baseSalary) => (
              <option key={baseSalary.id} value={baseSalary.id}>
                {baseSalary.baseSalary}
              </option>
            ))
          ) : (
            <option value={formData.id}>{formData.baseSalary}</option>
          )}
        </Form.Select>
      </FloatingLabel>
    </div>
  )
}
export default FormSelectBaseSalary
