import getAvatars from '../../models/photos';
import { getRememberMe, setRememberMe } from '../../utils/auths';
import {clearPage, renderLoading} from '../../utils/render';
import Navbar from '../Navbar/Navbar';
import Navigate from '../Router/Navigate';

const RegisterPage = async () => {
  clearPage();
  renderLoading();
  const avatars = await getAvatars();
  clearPage()
  renderRegisterForm(avatars);

  const radioButtons = document.getElementsByName('flexRadioDefault');

  for (let i = 0; i < radioButtons.length; i += 1) {
    radioButtons[i].addEventListener('click', handleRadioClick);
  }

  handleRadioClick();
  function handleRadioClick() {
    if (document.getElementById('radioAvatar').checked) {
      document.getElementById('photo-upload-form').style.display = 'none';
      document.getElementById('pick-avatar').style.display = 'block';
    } else {
      document.getElementById('photo-upload-form').style.display = 'block';
      document.getElementById('pick-avatar').style.display = 'none';
      document.getElementById('choosen-avatar').innerHTML = '';
    }
  }
};

function renderRegisterForm(avatars) {
  const main = document.querySelector('main');
  main.classList.add("no-scrollbar");
  const form = document.createElement('form');
  form.className = 'p-5';

  const divContainer = document.createElement('div');
  divContainer.className = 'container-fluid d-flex justify-content-center align-items-center h-75';

  const divForm = document.createElement('div');
  divForm.className = 'd-flex flex-column bg-white border rounded w-75';
  divForm.id = 'divForm';

  const divRowTitle = document.createElement('div');

  const title = document.createElement('h1');
  title.innerHTML = 'Inscription';
  title.className = 'text-center my-3';

  const divRow = document.createElement('div');
  divRow.className = 'row pe-3';

  const divColInputs = document.createElement('div');
  divColInputs.className = 'col';
  const divColAvatar = document.createElement('div');
  divColAvatar.className = 'col';

  const firstname = document.createElement('input');
  firstname.type = 'text';
  firstname.id = 'firstname';
  firstname.placeholder = 'Prénom';
  firstname.required = true;
  firstname.className = 'form-control mb-3';
  const lastname = document.createElement('input');
  lastname.type = 'text';
  lastname.id = 'lastname';
  lastname.placeholder = 'Nom';
  lastname.required = true;
  lastname.className = 'form-control mb-3';
  const email = document.createElement('input');
  email.type = 'email';
  email.id = 'email';
  email.required = true;
  email.placeholder = 'Email';
  email.className = 'form-control mb-3';
  const phone = document.createElement('input');
  phone.type = 'tel';
  phone.id = 'phone';
  phone.required = true;
  phone.placeholder = 'Numéro de téléphone';
  phone.className = 'form-control mb-3';
  const password = document.createElement('input');
  password.type = 'password';
  password.id = 'password';
  password.required = true;
  password.placeholder = 'Mot de passe';
  password.className = 'form-control mb-3';
  const passwordConfirmation = document.createElement('input');
  passwordConfirmation.type = 'password';
  passwordConfirmation.id = 'passwordConfirmation';
  passwordConfirmation.required = true;
  passwordConfirmation.placeholder = 'Confirmation du mot de passe';
  passwordConfirmation.className = 'form-control mb-3';

  const submit = document.createElement('input');
  submit.value = "S'inscrire";
  submit.type = 'submit';
  submit.className = 'btn btn-secondary';
  submit.id = 'fontColor';
  const formCheckWrapper = document.createElement('div');
  formCheckWrapper.className = 'mb-3 form-check';

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

  formCheckWrapper.appendChild(rememberme);
  formCheckWrapper.appendChild(checkLabel);

  const test = `
<p>
  <div class="form-check">
    <input class="form-check-input" type="radio" name="flexRadioDefault" id="radioPhoto" checked>
    <label class="form-check-label" for="flexRadioDefault2">
      Choisir une photo
    </label>
  </div>
  <div class="form-check">
  <input class="form-check-input" type="radio" name="flexRadioDefault" id="radioAvatar">
  <label class="form-check-label" for="flexRadioDefault1">
    Choisir un avatar
  </label>
  </div>
</p>



  <div id="photo-upload-form">
  <form>
<label>Choisir une photo</label>
<input class="form-control" name="file" type= "file" /> <br/><br/>
</form>
</div>

<div class="py-2" id="choosen-avatar"></div>

<div id="pick-avatar"></div>
`;

  divColAvatar.innerHTML = test;

  form.appendChild(firstname);
  form.appendChild(lastname);
  form.appendChild(email);
  form.appendChild(phone);
  form.appendChild(password);
  form.appendChild(passwordConfirmation);
  form.appendChild(formCheckWrapper);
  form.appendChild(submit);
  divColInputs.appendChild(form);
  divRow.appendChild(divColInputs);
  divRow.appendChild(divColAvatar);
  divRowTitle.appendChild(title);
  divForm.appendChild(divRowTitle);
  divForm.appendChild(divRow);
  divContainer.appendChild(divForm);
  main.appendChild(divContainer);
  renderChooseAvatar(avatars);
  form.addEventListener('submit', onRegister);
}

function renderChooseAvatar(avatars) {
  const divAvatar = document.getElementById('pick-avatar');
  const divChoosenAvatar = document.getElementById('choosen-avatar');
  divChoosenAvatar.innerHTML = '';

  divAvatar.className = 'bg-white p-3 w-75 h-50';
  divAvatar.style = 'overflow-y:auto';
  const divDisplayedAvatars = document.createElement('div');
  divDisplayedAvatars.className = 'row row-cols-2 h-50';

  avatars.forEach((avatar) => {
    const colAvatar = document.createElement('div');
    colAvatar.className = 'col mt-3';

    const imgAvatar = document.createElement('img');
    imgAvatar.className = 'rounded border border-secondary border-2 cursor-pointer';
    imgAvatar.src = avatar.accessPath;
    imgAvatar.width = 75;
    imgAvatar.heigth = 75;
    colAvatar.appendChild(imgAvatar);
    divDisplayedAvatars.appendChild(colAvatar);

    colAvatar.addEventListener('click', () => {
      divChoosenAvatar.innerText = 'Avatar choisi : ';
      divChoosenAvatar.value = avatar.accessPath;
      const imgChoosenAvatar = document.createElement('img');
      imgChoosenAvatar.className = 'rounded border border-secondary border-2';
      imgChoosenAvatar.src = avatar.accessPath;
      imgChoosenAvatar.width = 50;
      imgChoosenAvatar.heigth = 50;
      divChoosenAvatar.appendChild(imgChoosenAvatar);
    });
  });
  divAvatar.appendChild(divDisplayedAvatars);
}

function onCheckboxClicked(e) {
  setRememberMe(e.target.checked);
}

async function onRegister(e) {
  e.stopPropagation();
  e.preventDefault();

  const firstname = document.querySelector('#firstname').value;
  const lastname = document.querySelector('#lastname').value;
  const email = document.querySelector('#email').value;
  const phone = document.querySelector('#phone').value;
  const password = document.querySelector('#password').value;
  const passwordConfirmation = document.querySelector('#passwordConfirmation').value;
  const fileInput = document.querySelector('input[name=file]');
  const avatar = document.getElementById('choosen-avatar').value;

  const formData = new FormData();

  if (avatar != null) {
    formData.append('avatarPath',avatar);
  }
  else {
    formData.append('file', fileInput.files[0]);
  }
  formData.append('firstname', firstname);
  formData.append('lastname', lastname);
  formData.append('email', email);
  formData.append('phone', phone);
  formData.append('password', password);
  formData.append('passwordConfirmation', passwordConfirmation);
  const options = {
    method: 'POST',
    body: formData,
  };

  const response = await fetch(`/api/user/register`, options);

  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  /*
  const authenticatedUser = await response.json();

  setAuthenticatedUser(authenticatedUser);
  */
  Navbar();

  Navigate('/');
}

export default RegisterPage;
