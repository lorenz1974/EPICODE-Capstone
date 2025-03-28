import { useEffect, useState } from 'react'
import { Button, Modal } from 'react-bootstrap'

function ModalAlert({ alert = ['', ''], setModalAlert }) {
  const [showModal, setShowModal] = useState(false)
  const alertLevel = alert[0]
  const alertMsg = alert[1]

  switch (alertLevel) {
    case 'Error':
      var titleClass = 'text-danger'
      break
    case 'Warning':
      titleClass = 'text-warning'
      break
    case 'Info':
      titleClass = 'text-info'
      break
    case 'Success':
      titleClass = 'text-success'
      break
    default:
      titleClass = 'text-primary'
  }

  useEffect(() => {
    if (alertMsg) {
      setShowModal(true)
    }
  }, [alertMsg])

  const handleClose = () => {
    setModalAlert(['', ''])
    setShowModal(false)
  }

  return (
    <Modal
      show={showModal}
      onHide={handleClose}
      backdrop='static'
      keyboard={false}
    >
      <Modal.Header closeButton>
        <Modal.Title className={titleClass}>{alertLevel}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{alertMsg}</Modal.Body>
      <Modal.Footer>
        <Button type='button' variant='secondary' onClick={handleClose}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default ModalAlert
