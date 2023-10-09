import { getToken } from '../utils/auths';

const addAvailability = async (date) => {

  const token = getToken();

  const options = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token,
    },
    body: JSON.stringify({
      date,
    }),
  };

  const response = await fetch(`${process.env.API_BASE_URL}/availability/add`, options);

  if (!response.ok) {
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  }
}

const getAllAvailabilitiesDate = async () => {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/availability/all`);

    if (!response.ok) {
      throw new Error(`getAllAvailabilitiesDate:: fetch error : ${response.status} : ${response.statusText}`);
    }
    const films = await response.json();
    return films;
  } catch (err) {
    console.error('getAllAvailabilitiesDate::error: ', err);
    throw err;
  }
};
export {
  addAvailability,
  getAllAvailabilitiesDate,
};
