import React from 'react'
import {
  Row,
  Col,
  Button,
  Navbar,
  Nav,
  NavDropdown,
  Form,
  InputGroup,
} from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import {
  HOME,
  USERS,
  SIGN_REQUESTS,
  COMPANIES,
  BRANCHES,
  CITIES,
  SECTORS,
  LOGIN,
  LOGOUT,
  PROFILE,
  CONFIG,
  FILES,
  SIGN_REQUEST_CHRONOLOGIES,
  DOC_TEMPLATES,
  LOGIN_DATA,
} from '@constants'
import { FaSignature } from 'react-icons/fa6'
import { FaRegUserCircle } from 'react-icons/fa'
import { isAuthenticated, isAdmin, isRole } from '@utils/auth'
import { getUserFullName } from '@utils/functions'
import { useContext, useState, useEffect } from 'react'
import { Context } from '../App'
import { FaSearch } from 'react-icons/fa'
import { MdCancel } from 'react-icons/md'
import { getUserId } from '../utils/auth'
import {
  FaUsers,
  FaBuilding,
  FaList,
  FaCog,
  FaFileAlt,
  FaFolderOpen,
  FaUser,
  FaSignOutAlt,
  FaClock,
  FaEye,
} from 'react-icons/fa'
import { RiDashboard3Fill } from 'react-icons/ri'
import { LiaFileSignatureSolid } from 'react-icons/lia'
import { MdOutlineManageAccounts } from 'react-icons/md'
import { BsBuilding, BsPeople } from 'react-icons/bs'

const navBar = () => {
  const { q, setQ } = useContext(Context)
  const { pagination, setPagination } = useContext(Context)

  const [searchTerm, setSearchTerm] = useState('') // Local state for debouncing

  const navigate = useNavigate()

  useEffect(() => {
    const handler = setTimeout(() => {
      // Reset 'currentPage' to 0
      // setPagination((prev) => ({
      //   ...prev,
      //   currentPage: 0,
      // }))
      // Update 'q' after 500ms
      setQ(searchTerm)
    }, 500)

    return () => clearTimeout(handler) // Clear timeout on cleanup
  }, [searchTerm])

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value) // Update local state
  }

  return (
    <Navbar
      expand='lg'
      sticky='top'
      className='border border-warning bg-light mx-3 mt-1 mb-3 px-4 bg-body shadow'
    >
      <Navbar.Brand onClick={() => navigate(HOME)}>
        <FaSignature className='fs-1 me-4' />
      </Navbar.Brand>
      <Navbar.Toggle aria-controls='basic-navbar-nav' />

      <Navbar.Collapse id='basic-navbar-nav'>
        <Nav className='me-auto'>
          {/*

                            ANAGRAFICHE

        */}
          <div className='d-flex align-items-center'>
            <BsPeople className='fs-4' />
          </div>
          <NavDropdown
            title='Anagrafiche'
            id='user-details-nav-dropdown'
            className='me-2'
          >
            <NavDropdown.Item onClick={() => navigate(USERS)}>
              <FaUsers className='me-2' /> Utenti
            </NavDropdown.Item>
            <NavDropdown.Item onClick={() => navigate(`${USERS}/newuser`)}>
              <FaUser className='me-2' /> Nuovo
            </NavDropdown.Item>
          </NavDropdown>
          {/*

                            AZIENDE

        */}
          <div className='d-flex align-items-center'>
            <BsBuilding className='fs-5' />
          </div>
          <Nav.Link className='me-2' onClick={() => navigate(COMPANIES)}>
            Aziende
          </Nav.Link>
          {/*

                            FIRME

        */}
          <div className='d-flex align-items-center'>
            <LiaFileSignatureSolid className='fs-3' />
          </div>
          <NavDropdown title='Firme' id='signes-nav-dropdown' className='me-2'>
            <NavDropdown.Item onClick={() => navigate(HOME)}>
              <RiDashboard3Fill className='me-2' /> Dashboard
            </NavDropdown.Item>
            <NavDropdown.Item onClick={() => navigate(SIGN_REQUESTS)}>
              <FaList className='me-2' /> Elenco
            </NavDropdown.Item>
          </NavDropdown>
        </Nav>
        {/*

                            ADMINISTRATION

        */}
        {isAdmin() && (
          <Nav>
            <div className='d-flex align-items-center'>
              <MdOutlineManageAccounts className='fs-3' />
            </div>
            <NavDropdown title='Administration' id='admin-nav-dropdown'>
              <NavDropdown.Item onClick={() => navigate(CONFIG)}>
                <FaCog className='me-2' /> Configuration
              </NavDropdown.Item>
              <NavDropdown.Item
                onClick={() => navigate(SIGN_REQUEST_CHRONOLOGIES)}
              >
                <FaClock className='me-2' /> Requests Chronologies
              </NavDropdown.Item>
              <NavDropdown.Item onClick={() => navigate(LOGIN_DATA)}>
                <FaEye className='me-2' /> Login Data
              </NavDropdown.Item>
              <NavDropdown.Item onClick={() => navigate(DOC_TEMPLATES)}>
                <FaFileAlt className='me-2' /> Documents Templates
              </NavDropdown.Item>
              <NavDropdown.Item onClick={() => navigate(FILES)}>
                <FaFolderOpen className='me-2' /> Files
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>
        )}
        {/*

                            SEARCH

        */}
        <Nav className='m-3 px-3'>
          <Form onSubmit={(e) => e.preventDefault()}>
            <Row>
              <Col xs='auto'>
                {' '}
                <InputGroup>
                  <InputGroup.Text id='basic-addon1'>
                    <FaSearch />
                  </InputGroup.Text>
                  <Form.Control
                    type='text'
                    placeholder='Search'
                    className='mr-sm-2'
                    value={searchTerm} // Controlled input
                    onChange={handleSearchChange} // Update local state on input change
                  />
                  <Col
                    className='d-flex align-items-center ms-2 fs-4 text-danger'
                    onClick={() => setSearchTerm('')}
                  >
                    <MdCancel />
                  </Col>
                </InputGroup>
              </Col>
            </Row>
          </Form>
        </Nav>
        {/*

                            PROFILE MENU

        */}
        <Nav className='d-flex align-items-center'>
          {isAuthenticated() && getUserFullName()}{' '}
          <NavDropdown
            title={<FaRegUserCircle className='me-2 fs-5' />}
            id='user-nav-dropdown'
            align='end'
          >
            {!isAuthenticated() && (
              <NavDropdown.Item onClick={() => navigate(LOGIN)}>
                Login
              </NavDropdown.Item>
            )}
            {isAuthenticated() && (
              <>
                <NavDropdown.Item
                  onClick={() => navigate(USERS + '/' + getUserId())}
                >
                  <FaUser className='me-2' /> Profilo
                </NavDropdown.Item>
                <NavDropdown.Item onClick={() => navigate(LOGOUT)}>
                  <FaSignOutAlt className='me-2' /> Logout
                </NavDropdown.Item>
              </>
            )}
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  )
}

export default navBar
