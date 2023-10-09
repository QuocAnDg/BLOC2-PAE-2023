import { getRememberMe, setAuthenticatedUser, setRememberMe } from '../../utils/auths';
import { clearPage } from '../../utils/render';
import Navbar from '../Navbar/Navbar';
import Navigate from '../Router/Navigate';

const LoginPage = () => {
  clearPage();
  renderLoginForm();
};

function renderLoginForm() {
  const main = document.querySelector('main');
  main.classList.add("no-scrollbar");
  const banner = document.createElement('div');
  banner.className = 'p-3';

  const divForm = document.createElement('div');
  divForm.className = 'container d-flex flex-column bg-white border rounded mt-5';
  divForm.id = 'divForm';
  
  const form = document.createElement('form');

  const divTitle = document.createElement('div');
  divTitle.className = 'text-center m-4';
  const titleLogin = document.createElement('h2');
  titleLogin.innerText = 'Connexion';

  const divEmail = document.createElement('div');
  divEmail.className = 'input-group px-5';
  const iconEmail = document.createElement('span');
  iconEmail.className = 'input-group-text mb-3';
  iconEmail.innerHTML = `<i class="fa fa-solid fa-envelope"></i>`;
  const email = document.createElement('input');
  email.type = 'text';
  email.id = 'email';
  email.placeholder = 'Email';
  email.required = true;
  email.className = 'form-control mb-3';
  email.ariaLabel = 'email';

  const divPassword = document.createElement('div');
  divPassword.className = 'input-group px-5';
  const iconPassword = document.createElement('span');
  iconPassword.className = 'input-group-text mb-3';
  iconPassword.innerHTML = `<i class="fa fa-solid fa-lock"></i>`;
  const password = document.createElement('input');
  password.type = 'password';
  password.id = 'password';
  password.required = true;
  password.placeholder = 'Mot de passe';
  password.className = 'form-control mb-3';

  const divInput = document.createElement('div');
  divInput.className = 'container d-flex justify-content-center';

  const formCheckWrapper = document.createElement('div');
  formCheckWrapper.className = 'mb-3 form-check d-flex justify-content-center';

  const divRememberme = document.createElement('div');
  const rememberme = document.createElement('input');
  rememberme.type = 'checkbox';
  rememberme.className = 'form-check-input';
  rememberme.id = 'rememberme';
  const remembered = getRememberMe();
  rememberme.checked = remembered;
  rememberme.addEventListener('click', onCheckboxClicked);

  const checkLabel = document.createElement('label');
  checkLabel.htmlFor = 'rememberme';
  checkLabel.className = 'form-check-label';
  checkLabel.textContent = 'Rester connecté';

  divRememberme.appendChild(rememberme);
  divRememberme.appendChild(checkLabel)
  formCheckWrapper.appendChild(divRememberme);

  const submit = document.createElement('input');
  submit.value = 'Se connecter';
  submit.type = 'submit';
  submit.className = 'btn text-white';
  submit.id = 'fontColor';

  const divNoAccount = document.createElement('div');
  divNoAccount.className = 'd-inline-flex ms-3 mt-4';

  const divTextNoAccount = document.createElement('div');
  divTextNoAccount.className = 'row d-inline-flex';

  const textNoAccount = document.createElement('p');
  textNoAccount.className = 'col-auto pe-0';
  textNoAccount.innerText = 'Pas encore inscrit ? S\'inscrire';

  const linkRegister = document.createElement('p');
  linkRegister.className = 'col-auto text-primary px-1';
  linkRegister.id = 'linkRegister';
  linkRegister.style.cursor = 'pointer';
  linkRegister.innerText = ' ici';

  const loginErrorDiv = document.createElement('div');
  loginErrorDiv.className = "login-error-div";
  const loginErrorP = document.createElement('p');
  loginErrorP.className = "login-error-p";

  linkRegister.addEventListener('click',() => {

    Navigate('/register');
  });
  loginErrorDiv.appendChild(loginErrorP);
  divTitle.appendChild(titleLogin);
  form.appendChild(divTitle);
  divEmail.appendChild(iconEmail);
  divEmail.appendChild(email);
  divPassword.appendChild(iconPassword);
  divPassword.appendChild(password);
  form.appendChild(divEmail);
  form.appendChild(divPassword);
  form.appendChild(formCheckWrapper);
  divInput.appendChild(submit);
  form.appendChild(divInput);
  form.appendChild(loginErrorDiv);
  divTextNoAccount.appendChild(textNoAccount);
  divTextNoAccount.appendChild(linkRegister);
  divNoAccount.appendChild(divTextNoAccount);
  divForm.appendChild(form);
  divForm.appendChild(divNoAccount);
  banner.appendChild(divForm);
  main.appendChild(banner);
  form.addEventListener('submit', onLogin);
}

function onCheckboxClicked(e) {
  setRememberMe(e.target.checked);
}

async function onLogin(e) {
  e.preventDefault();

  const email = document.querySelector('#email').value;
  const password = document.querySelector('#password').value;

  const options = {
    method: 'POST',
    body: JSON.stringify({
      email,
      password,
    }),
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const response = await fetch(`${process.env.API_BASE_URL}/user/login`, options);

  if (!response.ok) {
    const errorP = document.querySelector('.login-error-p');
    errorP.classList.add('text-center', 'text-danger');
    errorP.innerText = "Email ou mot de passe incorrect";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  }

  const authenticatedUser = await response.json();

  setAuthenticatedUser(authenticatedUser);

  Navbar();

  Navigate('/');
}

export default LoginPage;
