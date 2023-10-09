import { getToken } from "../utils/auths";

const readAllItems = async () => {
  try {
    const options = {
      method: 'GET',
      headers: {
        'Authorization': getToken()
      }
    }
    const response = await fetch(`${process.env.API_BASE_URL}/items/all`, options);
    if (!response.ok) {
      throw new Error(`readAllItems:: fetch error : ${response.status} : ${response.statusText}`);
    }
    const items = await response.json();
    return items;
  } catch (err) {
    console.error('readAllItems::error: ', err);
    throw err;
  }
};

const readHomePageItems = async () => {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/items/startingitems`);
    if (!response.ok) {
      throw new Error(`readHomePageItems:: fetch error : ${response.status} : ${response.statusText}`);
    }
    const items = await response.json();
    return items;
  } catch (err) {
    console.error('readHomePageItems::error: ', err);
    throw err;
  }
};

const readAllItemsFromUser = async (id) => {
  try {
    const options = {
      method: 'GET',
      headers: {
        'Authorization': getToken()
      }
    }
    const response = await fetch(`${process.env.API_BASE_URL}/items/all/${id}`, options);

    if (!response.ok) {
      throw new Error(`readAllItemsFromUser :: fetch error : ${response.status} : ${response.statusText}`);
    }
    const items = await response.json();

    return items;
  } catch (err) {
    console.error('readAllItemsFromUser::error: ', err);
    throw err;
  }
};

const readOneItem = async () => {
  try {
    const urlParams = new URLSearchParams(window.location.search);

    const id = urlParams.get('id');

    const response = await fetch(`${process.env.API_BASE_URL}/items/${id}`);

    if (!response.ok) {
      throw new Error(`readOneItem :: fetch error : ${response.status} : ${response.statusText}`);
    }
    const items = await response.json();

    return items;
  } catch (err) {
    console.error('readOneItem::error: ', err);
    throw err;
  }
};

const getAllOfferedItems = async () => {
  const token = getToken();
  try{
    const options = {
      method : 'GET',
      headers:{
        'Authorization': token
      },
    }
    const response = await fetch(`${process.env.API_BASE_URL}/items/allOffered`, options);
    if (!response.ok) {
      throw new Error(`getAllOfferedItems :: fetch error : ${response.status} : ${response.statusText}`);
    }
    const offeredItems = await response.json();
    return offeredItems;
  } catch (err) {
    console.error('readAllItemsFromUser::error: ', err);
    throw err;
  }
}

const denyOfferedItem = async (explicationDeny, itemId) => {
  const token = getToken();
  try{
    const options = {
      method : 'PATCH',
      headers:{
        'Content-Type': 'text/plain',
        'Authorization': token
      },
      body : explicationDeny,
    }
    const response = await fetch(`${process.env.API_BASE_URL}/items/deny/${itemId}`, options);

    if (!response.ok) {
      throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
    }
    await response.json();
  }catch (err) {
    console.error('readAllItemsFromUser::error: ', err);
    throw err;
  }

}

const acceptOfferedItem = async (itemId) => {
  const token = getToken();
  try{
    const options = {
      method : 'PATCH',
      headers:{
        'Authorization': token
      },
    }
    const response = await fetch(`${process.env.API_BASE_URL}/items/confirm/${itemId}`, options);

    if (!response.ok) {
      throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
    }
    await response.json();
  }catch (err) {
    console.error('readAllItemsFromUser::error: ', err);
    throw err;
  }
}

async function updateState(id, state) {
  const response = await fetch(`${process.env.API_BASE_URL}/items/updateState/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'text/plain',
      'Authorization': getToken(),
    },
    body: state,
  });

  if (!response.ok)
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function updatePhoto(id, fileInput) {
  const formData = new FormData();
  formData.append('file', fileInput.files[0]);

  const response = await fetch(`${process.env.API_BASE_URL}/items/updatePhoto/${id}`, {
    method: 'PATCH',
    headers: {
      'Authorization': getToken(),
    },
    body: formData,
  });

  if (!response.ok)
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function updateStateForSale(id, price) {
  const response = await fetch(`${process.env.API_BASE_URL}/items/updateState/${id}/${price}`, {
    method: 'PATCH',
    headers: {
      'Authorization': getToken(),
    }
  });

  if (!response.ok)
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function updateItemInformation(id, description, type, version) {
  const response = await fetch(`${process.env.API_BASE_URL}/items/updateInformation/${id}/`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken(),
    },
    body: JSON.stringify({
      description,
      type,
      version
    }),
  });

  if (!response.ok)
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function getAllItemTypes(){
  const response = await fetch(`${process.env.API_BASE_URL}/items/allTypes`)
  if (!response.ok)
  throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function searchByName(name) {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/items/search?name=${name}`);
    if (!response.ok) {
      throw new Error(`searchByName:: fetch error : ${response.status} : ${response.statusText}`);
    }
    const items = await response.json();
    return items;
  } catch (err) {
    console.error('searchByName::error: ', err);
    throw err;
  }
}

async function updateStateSoldFromInStore(id, price) {
  const response = await fetch(`${process.env.API_BASE_URL}/items/sellFromStore/${id}/${price}`, {
    method: 'PATCH',
    headers: {
      'Authorization': getToken(),
    }
  });

  if (!response.ok)
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  return response.json();
}

async function searchByType(type) {
  try {
    console.log(`${process.env.API_BASE_URL}/items/typesearch?type=${type}`);
    const response = await fetch(`${process.env.API_BASE_URL}/items/typesearch?type=${type}`);

    if (!response.ok) {
      return [];
    }
    const items = await response.json();
    if (items.length === 0) {
      return [];
    }
    return items;

  } catch (err) {
    console.error('searchByType::error: ', err);
    throw err;
  }
}

async function searchByState(state) {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/items/statesearch?state=${state}`);

    if (!response.ok) {
      return [];
    }
    const items = await response.json();
    if (items.length === 0) {
      return [];
    }
    return items;
  } catch (err) {
    console.error('searchByState::error: ', err);
    throw err;
  }
}

export {
    readAllItems,
    readHomePageItems,
    readAllItemsFromUser,
    readOneItem,
    getAllOfferedItems,
    denyOfferedItem,
    acceptOfferedItem,
    updateState,
    updateStateForSale,
    getAllItemTypes,
    updateItemInformation,
    searchByName,
    updatePhoto,
    updateStateSoldFromInStore,
    searchByType,
    searchByState
};