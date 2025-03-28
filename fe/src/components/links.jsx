import { Route, Routes, useParams } from 'react-router-dom'
import { useState } from 'react'
import {
  HOME,
  USERS,
  LOGIN,
  PROFILE,
  JOB_PROFILES,
  SIGN_REQUESTS,
  CONFIG,
  FILES,
  SIGN_REQUEST_CHRONOLOGIES,
  LOGIN_DATA,
  DOC_TEMPLATES,
  COMPANIES,
  DASHBOARD,
  LOGOUT,
} from '@constants'
import Home from '@pages/Home'
import Users from '@pages/Users'
import Login from '@pages/Login'
import Profile from '@pages/Profile'
import SignRequests from '@pages/SignRequests'
import Config from '@pages/Config'
import Files from '@pages/Files'
import SignRequestChronologies from '@pages/SignRequestChronologies'
import LoginData from '@pages/LoginData'
import DocTemplates from '@pages/DocTemplates'
import Companies from '@pages/Companies'
import Dashboard from '@pages/Dashboard'
import Logout from '@pages/Logout'
import Page404 from '@pages/Page404'
import { isAuthenticated } from '@utils/auth'

const Links = () => {
  const [authenticated, setAuthenticated] = useState(isAuthenticated())

  const ProfileWithId = () => {
    const { id } = useParams()
    return <Profile id={id} />
  }
  const ConfigWithId = () => {
    const { id } = useParams()
    return <Config id={id} />
  }

  const DocTemplatesWithId = () => {
    const { id } = useParams()
    return <DocTemplates id={id} />
  }

  return (
    <Routes>
      <Route path={HOME} element={isAuthenticated() ? <Home /> : <Login />} />
      <Route path={USERS} element={isAuthenticated() ? <Users /> : <Login />} />
      <Route
        path={USERS + '/:id'}
        element={isAuthenticated() ? <ProfileWithId /> : <Login />}
      />
      <Route path={LOGIN} element={<Login />} />

      <Route
        path={PROFILE}
        element={isAuthenticated() ? <Profile /> : <Login />}
      />
      <Route
        path={SIGN_REQUESTS}
        element={isAuthenticated() ? <SignRequests /> : <Login />}
      />
      <Route
        path={CONFIG}
        element={isAuthenticated() ? <Config /> : <Login />}
      />
      <Route
        path={CONFIG + '/:id'}
        element={isAuthenticated() ? <ConfigWithId /> : <Login />}
      />
      <Route path={FILES} element={isAuthenticated() ? <Files /> : <Login />} />
      <Route
        path={SIGN_REQUEST_CHRONOLOGIES}
        element={isAuthenticated() ? <SignRequestChronologies /> : <Login />}
      />
      <Route
        path={LOGIN_DATA}
        element={isAuthenticated() ? <LoginData /> : <Login />}
      />
      <Route
        path={DOC_TEMPLATES}
        element={isAuthenticated() ? <DocTemplates /> : <Login />}
      />
      <Route
        path={DOC_TEMPLATES + '/:id'}
        element={isAuthenticated() ? <DocTemplatesWithId /> : <Login />}
      />
      <Route
        path={COMPANIES}
        element={isAuthenticated() ? <Companies /> : <Login />}
      />
      <Route
        path={DASHBOARD}
        element={isAuthenticated() ? <Dashboard /> : <Login />}
      />
      <Route
        path={LOGOUT}
        element={isAuthenticated() ? <Logout /> : <Login />}
      />
      <Route path='/*' element={isAuthenticated() ? <Page404 /> : <Login />} />
    </Routes>
  )
}

export default Links
