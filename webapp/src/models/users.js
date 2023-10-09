import { getToken } from '../utils/auths';

const readAllUsers = async () => {
    const token = getToken();

    const options = {
      method: 'GET',
      headers: {
        'Authorization': token
      }
    }
  
    const response = await fetch(`${process.env.API_BASE_URL}/user`,options);
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  
    const registrations = await response.json();
    return registrations;
}

const updateRoleUser = async (id, newRole) => {

    const token = getToken();

    const options = {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token,
        },
      body: JSON.stringify({
        'newRole': newRole,
      })
    };
    
    const response = await fetch(`${process.env.API_BASE_URL}/user/`.concat(id), options);
    
    if (!response.ok) {
      throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
    }
}

const editUser = async (id, editedUserData) => {

  const token = getToken();

  const options = {
    method: 'PATCH',
    headers: {
      'Authorization': token,
    },
    body: editedUserData,
  };

  const response = await fetch(`${process.env.API_BASE_URL}/user/editUser/`.concat(id), options);

  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
}

const changePassword = async (id, oldPassword, newPassword) => {

  const token = getToken();

  const options = {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      'oldPassword': oldPassword,
      'newPassword': newPassword
    })
  }

  const response = await fetch(`${process.env.API_BASE_URL}/user/changePassword/`.concat(id), options);
  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  const user = await response.json();
  return user;
}

export {readAllUsers, updateRoleUser, editUser, changePassword} ;