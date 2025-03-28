import React from 'react'
import { Placeholder } from 'react-bootstrap'
const CustomPlaceholder = () => {
  return (
    <Placeholder animation='glow'>
      <Placeholder xs={7} /> <Placeholder xs={4} /> <Placeholder xs={4} />{' '}
      <Placeholder xs={6} /> <Placeholder xs={8} />
    </Placeholder>
  )
}

export default CustomPlaceholder
