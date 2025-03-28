const Avatar = ({ sex = 'm', id = 0, className }) => {
  // Function to generate the image uri
  const generateImageUri = (sex, id) => {
    if (id === 0) return '/assets/images/avatar.png'
    return `/assets/images/avatar-${sex.toLowerCase()}-${id}.jpg`
  }

  // Function to get a number from an id (text or number)
  const getNumberFromId = (id) => {
    const numericId =
      typeof id === 'string'
        ? id.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0)
        : id
    return (numericId % 4) + 1
  }

  const number = getNumberFromId(id)
  const imageUri = generateImageUri(sex, number)

  return (
    <img
      className={className}
      src={imageUri}
      alt={`User Avatar - n. ${number}`}
    />
  )
}

export default Avatar
