import { Table } from 'react-bootstrap'
import FormDivider from './formDivider'

const BrancesTable = ({ branches = {}, branchId, setBranchId }) => {
  return (
    <>
      <Table>
        <tbody>
          {branches && branches.length > 0 ? (
            branches.map((branch) => (
              <tr
                className='cursor-pointer'
                key={branch.id}
                onClick={() => {
                  console.log('Branch selected: ', branch.id)
                  setBranchId(branch.id)
                }}
              >
                <td
                  className={
                    branch.id === branchId ? 'bg-secondary-subtle' : ''
                  }
                >
                  <p className='m-0 p-0 ' style={{ fontSize: '0.8em' }}>
                    <span className='fw-bold'>
                      {branch.description || 'N/A'}
                    </span>{' '}
                    : {''}
                    {branch.address || 'N/A'} - {branch.city || 'N/A'} (
                    {branch.province || 'N/A'}) - {branch.cap || 'N/A'}
                  </p>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan='2'>No branches available</td>
            </tr>
          )}
        </tbody>
      </Table>
    </>
  )
}
export default BrancesTable
