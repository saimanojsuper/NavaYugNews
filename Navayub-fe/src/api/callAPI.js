
const callAPI = async({pageNumber, pageSize, setArticlesData})=> {

    const url = 'api/currentNews'
    const jsonBody = JSON.stringify({
        pageNumber: pageNumber,
        pageSize: pageSize,
        fromDate: getCurrentDate(),
        toDate: getCurrentDate(),
        guardianAPIKey: process.env.REACT_APP_GUARDIAN_API_KEY,
        nyTimesAPIKey: process.env.REACT_APP_NEWYORKTIMES_API_KEY

    });

    console.log('env var', process.env)

    console.log('jsonbody', jsonBody);

    const response = await fetch(url,
        {
            method: "POST",
            mode: "cors",
            body: jsonBody,
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
                'Access-Control-Allow-Origin' : '*'
            }
        }
    );

    const data = await response.json();

    setArticlesData(data);


    console.log(' response', data);

    return data;

}

const getCurrentDate = () => {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based
    const day = String(date.getDate()).padStart(2, '0') - 1;
    
    return `${year}-${month}-${day}`;
};

export default callAPI;