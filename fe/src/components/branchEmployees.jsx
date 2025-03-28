import { useEffect, useState } from 'react'
import { Alert, Spinner } from 'react-bootstrap'
import { getData } from '@utils/api'
import { isAuthenticated } from '@utils/auth'
import { SERVER_BRANCHES } from '@endpoints'
import { useNavigate } from 'react-router-dom'
import { LOGOUT, USERS } from '@constants'
import CustomPlaceholder from '@components/customPlaceholder'

const BranchEmployees = ({ branchId }) => {
  const navigate = useNavigate()

  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    pageSize: 10,
    totalElements: 0,
  })
  const [employees, setEmployees] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [noDataMsg, setNoDataMsg] = useState('')

  useEffect(() => {
    const fetchEmployees = async () => {
      if (!branchId) {
        return
      }
      setIsLoading(true)
      setErrorMsg('')
      setNoDataMsg('')
      try {
        const params = {
          page: pagination.currentPage,
          size: pagination.pageSize,
          sortBy: 'surnameName',
          direction: 'ASC',
          alsoDeleted: false,
        }
        const data = await getData(
          SERVER_BRANCHES + '/' + branchId + '/employees',
          params
        )
        if (data.logout) {
          navigate(LOGOUT)
        } else if (data.isSuccess) {
          setIsLoading(false)
          if (data.page.totalElements !== 0) {
            setEmployees(data._embedded.appUserList)

            setPagination((prev) => ({
              ...prev,
              totalPages: data.page.totalPages,
              currentPage: data.page.number,
              pageSize: data.page.size,
              totalElements: data.page.totalElements,
            }))

            console.log('Fetched Branch Employees: ', data)
          } else {
            setEmployees([])
            setNoDataMsg('No data found')
          }
        } else {
          setErrorMsg(data.message)
        }
      } catch (error) {
        setIsLoading(false)
        setErrorMsg(error.message)
        console.error('Error fetching data: ', error.message)
      }
    }
    if (isAuthenticated()) {
      fetchEmployees()
    }
  }, [pagination.currentPage, branchId])
  return (
    <>
      {/* <p>[{branchId}]</p> */}
      {errorMsg && <Alert variant='danger'>{errorMsg}</Alert>}
      {noDataMsg && <div>{noDataMsg}</div>}
      {isLoading && <CustomPlaceholder />}
      {employees && !isLoading && !errorMsg && !noDataMsg && (
        <div>
          <ul>
            {employees.map((employee) => (
              <li
                className=' cursor-pointer'
                onClick={() => {
                  console.log(employee)
                  navigate(USERS + '/' + employee.id)
                }}
                key={employee.id}
              >
                {employee.surnameName}
              </li>
            ))}
          </ul>
        </div>
      )}
    </>
  )
}
export default BranchEmployees
