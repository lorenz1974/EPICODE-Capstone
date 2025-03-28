import { useContext } from 'react'
import { Context } from '../App'

function Login() {
  const { setAuthenticated } = useContext(Context)

  const handleLogin = async () => {
    try {
      // ...existing login logic...
      setAuthenticated(true) // Ensure this function is called correctly
    } catch (error) {
      console.error('Login failed:', error)
    }
  }

  return (
    // ...existing JSX...
    <button onClick={handleLogin}>Login</button>
  )
}

export default Login
