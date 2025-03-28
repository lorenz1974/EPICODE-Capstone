export const checkCf = async (e) => {
    //http://api.miocodicefiscale.com/calculate?lname={cognome}&fname={nome}&gender={sesso}&city={luogo-di-nascita}&state={codice-provincia}&abolished={comune-soppresso}&day={giorno-di-nascita}&month={mese-di-nascita}&year={anno-di-nascita}&omocodia_level={livello-omocodia}&access_token={tua-chiave-API}

    console.log('--- checkCf')
    const access_token = import.meta.env.VITE_CF_ACCESS_TOKEN
    const cfApiUsr = 'http://api.miocodicefiscale.com/calculate'

    const fetchCf = async () => {
        const birthDate = new Date(userData.birthDate)
        const params = {
            lname: userData.surname,
            fname: userData.name,
            gender: userData.gender,
            city: userData.birthPlace,
            //state: userData.birthProvince, // the short name of the province is needed in case
            day: birthDate.getDate(),
            month: birthDate.getMonth() + 1, // Months are zero-based
            year: birthDate.getFullYear(),
            access_token: access_token,
        }

        try {
            const data = await getData(`${cfApiUsr}`, params)
            console.log('--- data: ', data)
            return data.cf || ''
        } catch (error) {
            if (error instanceof TypeError) {
                // TBD
                //setErrorMsg('Fiscal Code not found')
            } else {
                // TBD
                //setErrorMsg('Server fetching error: ' + error.message)
            }
            return ''
        } finally {
            // TBD
            // setIsLoading(false)
        }
    }

    let cf = e.target.value
    if (
        cf.length === 0 &&
        userData.name.length != 0 &&
        userData.surname.length != 0 &&
        userData.birthDate.length != 0 &&
        userData.birthPlace.length != 0
    ) {
        console.log('--- fetchCf')
        return fetchCf()
    }
}