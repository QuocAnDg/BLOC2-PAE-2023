const STORE_NAME = 'user';
const REMEMBER_ME = 'remembered';

let currentUser;

const getAuthenticatedUser = async () => {
  const remembered = getRememberMe();
  const serializedToken = remembered
    ? localStorage.getItem(STORE_NAME)
    : sessionStorage.getItem(STORE_NAME);

  if (!serializedToken) return undefined;

  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': serializedToken,
    },
  };

  const response = await fetch(`${process.env.API_BASE_URL}/user/auth`, options);
  if (!response.ok) {
    currentUser = undefined;
  } else {
    currentUser = await response.json();
  }
  return currentUser;
};

const getToken = () => {
  const remembered = getRememberMe();

  if (remembered) return localStorage.getItem(STORE_NAME);
  return sessionStorage.getItem(STORE_NAME);
}

const setAuthenticatedUser = (authenticatedUser) => {
  const serializedToken = authenticatedUser.token;
  const remembered = getRememberMe();
  if (remembered) localStorage.setItem(STORE_NAME, serializedToken);
  else sessionStorage.setItem(STORE_NAME, serializedToken);

  currentUser = authenticatedUser.user;
};

const isAuthenticated = () => currentUser !== undefined;

const clearAuthenticatedUser = () => {
  localStorage.clear();
  sessionStorage.clear();
  currentUser = undefined;
};

function getRememberMe() {
  const rememberedSerialized = localStorage.getItem(REMEMBER_ME);
  const remembered = JSON.parse(rememberedSerialized);
  return remembered;
}

function setRememberMe(remembered) {
  const rememberedSerialized = JSON.stringify(remembered);
  localStorage.setItem(REMEMBER_ME, rememberedSerialized);
}

export {
  getAuthenticatedUser,
  getToken,
  setAuthenticatedUser,
  isAuthenticated,
  clearAuthenticatedUser,
  getRememberMe,
  setRememberMe,
};
