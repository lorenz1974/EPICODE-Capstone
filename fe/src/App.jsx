import React from 'react'
import { useState, createContext, useEffect } from 'react'
import { BrowserRouter as Router } from 'react-router-dom'
import { Alert, Container } from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import './App.css'
import { isAuthenticated } from '@utils/auth'
import Links from '@components/links'
import NavBar from '@components/navBar'
import Footer from '@components/footer'
import ModalAlert from './components/modalAlert'

export const Context = createContext()

function App() {
  const [authenticated, setAuthenticated] = useState(isAuthenticated())
  const [q, setQ] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const [alertMsg, setAlertMsg] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    pageSize: 25,
    totalElements: 0,
  })

  return (
    <Context.Provider
      value={{
        setAuthenticated,
        q,
        setQ,
        pagination,
        setPagination,
      }}
    >
      {' '}
      {/* Updated context value */}
      <Router>
        <Container
          fluid
          className='d-flex flex-column min-vh-100 video-container'
        >
          {!authenticated && (
            <video autoPlay muted loop>
              <source
                src='/assets/videos/signature-video-1-240.mp4'
                type='video/mp4'
              />
            </video>
          )}

          <header className='sticky-top'>
            {isAuthenticated() ? <NavBar /> : null}
          </header>
          <main className='container flex-grow-1 d-flex flex-column justify-content-start align-items-center'>
            <Links />
          </main>
          <footer>{isAuthenticated() ? <Footer /> : null}</footer>
        </Container>
      </Router>
    </Context.Provider>
  )
}

export default App
