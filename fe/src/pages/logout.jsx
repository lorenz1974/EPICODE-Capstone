import { Context } from '../App.jsx'
import { useContext, useEffect } from 'react'
import { Navigate } from 'react-router-dom'

const Logout = () => {
  const { setAuthenticated } = useContext(Context)

  useEffect(() => {
    localStorage.clear()
    sessionStorage.clear()
    setAuthenticated(false)
  }, [setAuthenticated]) // Add setAuthenticated as a dependency

  return <Navigate to='/' />
}

export default Logout
