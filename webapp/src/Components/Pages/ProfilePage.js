import { readAllItemsFromUser } from '../../models/items';
import getAvatars from '../../models/photos';
import { changePassword, editUser } from '../../models/users';
import { getAuthenticatedUser } from '../../utils/auths';
import {clearPage, renderLoading} from '../../utils/render';
import { itemStateFrenchTranslation } from '../../utils/translator';
import Navigate from '../Router/Navigate';
import {replaceDotWithComma} from "../../utils/utils";

let otherUser = false;

const ProfilePage = async (registration) => {

  renderLoading();
  let user;
  if(registration===undefined){
    user = await getAuthenticatedUser();
  } else {
    user = registration;
    otherUser = true;
  }
  clearPage();
  if (user === undefined) {
    Navigate('/login');
  } else {
    clearPage();
    renderProfilePage(user);
  }
};

let initialItems;

async function renderProfilePage(user) {
  const main = document.querySelector('main');
  main.classList.add("no-scrollbar");
  const section = document.createElement("section");
  section.classList.add("h-100");

  const divProfilePage = document.createElement('div');
  divProfilePage.className = 'container-fluid h-100';
  divProfilePage.style.overflow = 'hidden';

  const rowProfilePage = document.createElement('div');
  rowProfilePage.className = 'row h-100';
  rowProfilePage.id = 'rowProfilePage';

  const divMenu = document.createElement('div');
  divMenu.className = 'col-2 bg-white border-end border-primary p-3';

  const divUserPhotoLnFn = document.createElement('div');
  divUserPhotoLnFn.className = 'row justify-content-between mb-3';

  const divUserPhoto = document.createElement('div');
  divUserPhoto.className = 'col-4';
  const userPhoto = document.createElement('img');
  userPhoto.src = user.profilePhoto.accessPath;
  userPhoto.width = 65;
  userPhoto.height = 65;
  userPhoto.className = 'rounded-circle border border-dark';
  divUserPhoto.appendChild(userPhoto);

  const divUserLnFn = document.createElement('div');
  divUserLnFn.className = 'col';
  const userLnFn = document.createElement('div');
  userLnFn.className = 'fw-bold fs-4';
  userLnFn.innerText = user.lastname.concat(' ', user.firstname);
  divUserLnFn.appendChild(userLnFn);

  divUserPhotoLnFn.appendChild(divUserPhoto);
  divUserPhotoLnFn.appendChild(divUserLnFn);

  const menu = document.createElement('ul');
  menu.className = 'list-group list-group-flush';

  const itemsItem = document.createElement('li');
  itemsItem.className = 'list-group-item active cursor-pointer';
  itemsItem.innerText = 'Mes objets';

  const userInfoItem = document.createElement('li');
  userInfoItem.className = 'list-group-item cursor-pointer';
  userInfoItem.innerText = 'Mes informations';

  if (otherUser === false){
    const changePasswordItem = document.createElement('li');
    changePasswordItem.className = 'list-group-item cursor-pointer';
    changePasswordItem.innerText = 'Changer de mot de passe';
  
    menu.appendChild(itemsItem);
    menu.appendChild(userInfoItem);
    menu.appendChild(changePasswordItem);
    divMenu.appendChild(divUserPhotoLnFn);
    divMenu.appendChild(menu);
    rowProfilePage.appendChild(divMenu);
  
    const divSelectedOptMenu = document.createElement('div');
    divSelectedOptMenu.id = 'divSelectedOptMenu';
    divSelectedOptMenu.className = 'col p-3 px-5';
  
    rowProfilePage.appendChild(divSelectedOptMenu);
  
    divProfilePage.appendChild(rowProfilePage);
    section.appendChild(divProfilePage);
    main.appendChild(section);
  
    initialItems = await readAllItemsFromUser(user.id);
  
    renderUserItems(user, initialItems);
  
    itemsItem.addEventListener('click', () => {
      itemsItem.classList.add('active');
      userInfoItem.classList.remove('active');
      changePasswordItem.classList.remove('active');
      renderUserItems(user, initialItems);
    });
    userInfoItem.addEventListener('click', () => {
      itemsItem.classList.remove('active');
      userInfoItem.classList.add('active');
      changePasswordItem.classList.remove('active');
      renderUserInformation(user);
    });
    changePasswordItem.addEventListener('click', () => {
      itemsItem.classList.remove('active');
      userInfoItem.classList.remove('active');
      changePasswordItem.classList.add('active');
      renderChangeUserPassword(user);
    });
  } else {
  menu.appendChild(itemsItem);
  menu.appendChild(userInfoItem);
  divMenu.appendChild(divUserPhotoLnFn);
  divMenu.appendChild(menu);
  rowProfilePage.appendChild(divMenu);

  const divSelectedOptMenu = document.createElement('div');
  divSelectedOptMenu.id = 'divSelectedOptMenu';
  divSelectedOptMenu.className = 'col p-3 px-5';

  rowProfilePage.appendChild(divSelectedOptMenu);

  divProfilePage.appendChild(rowProfilePage);
  section.appendChild(divProfilePage);
  main.appendChild(section);

  initialItems = await readAllItemsFromUser(user.id);

  renderUserItems(user, initialItems);

  itemsItem.addEventListener('click', () => {
    itemsItem.classList.add('active');
    userInfoItem.classList.remove('active');
    renderUserItems(user, initialItems);
  });
  userInfoItem.addEventListener('click', () => {
    itemsItem.classList.remove('active');
    userInfoItem.classList.add('active');
    renderUserInformation(user);
  });
  }
}

function renderUserItems(user, items) {

  const divUserItems = document.getElementById('divSelectedOptMenu');
  divUserItems.innerHTML = '';

  if (items.length !== 0) {
    const rowSearchBar = document.createElement('div');
    rowSearchBar.className = 'row mt-4';

    const divSearchBar = document.createElement('div');
    divSearchBar.className = 'col-4';

    const inputGroupSearchBar = document.createElement('div');
    inputGroupSearchBar.className = 'homepage-search';

    const searchBar = document.createElement('input');
    searchBar.className = 'search-input';
    searchBar.type = 'text';
    searchBar.placeholder = 'Rechercher un objet par nom';

    const divSuggestion = document.createElement('div');
    divSuggestion.className = 'suggestions-box';

    const iconSearch = document.createElement('i');
    iconSearch.className = 'fa-solid fa-magnifying-glass';

    inputGroupSearchBar.appendChild(searchBar);
    inputGroupSearchBar.appendChild(divSuggestion);
    inputGroupSearchBar.appendChild(iconSearch);
    divSearchBar.appendChild(inputGroupSearchBar);
    
    rowSearchBar.appendChild(divSearchBar);

    const rowDisplayedItems = document.createElement('div');
    rowDisplayedItems.className = 'row mt-3 h-75';

    const divDisplayedItems = document.createElement('div');
    divDisplayedItems.className = 'col bg-white border h-75 mx-2';
    divDisplayedItems.style = 'overflow-y:auto';

    const gridCards = document.createElement('div');
    gridCards.className = 'row row-cols-1 row-cols-md-3 g-4 p-2';

    items.forEach((item) => {
      const colItem = document.createElement('div');
      colItem.className = 'col';

      const cardItem = document.createElement('div');
      cardItem.className = 'card';

      const stateItem = document.createElement('div');
      stateItem.className = 'card-header text-center';
      stateItem.innerText = itemStateFrenchTranslation(item.state);
      cardItem.appendChild(stateItem);

      const divItem = document.createElement('div');
      divItem.className = 'card-body';

      const divImgItem = document.createElement('div');
      divImgItem.className = 'container image-block-user-item';

      const imgItem = document.createElement('img');
      imgItem.src = item.photo.accessPath;
      imgItem.className = 'img-fluid h-auto';
      divImgItem.appendChild(imgItem);
      divItem.appendChild(divImgItem);
      cardItem.appendChild(divItem);

      const infoItem = document.createElement('div');
      infoItem.className = 'card-footer';
      const rowInfoItem = document.createElement('div');
      rowInfoItem.className = 'row';

      const nameItem = document.createElement('div');
      nameItem.innerText = item.description;
      nameItem.className = 'col';
      rowInfoItem.appendChild(nameItem);

      if (item.price !== 0) {
        const priceItem = document.createElement('div');
        priceItem.className = 'col-2';
        priceItem.innerText = `${replaceDotWithComma(item.price)}`.concat(" €");
        rowInfoItem.appendChild(priceItem);
      }

      infoItem.appendChild(rowInfoItem);
      cardItem.appendChild(infoItem);
      colItem.appendChild(cardItem);
      gridCards.appendChild(colItem);
    });

    divDisplayedItems.appendChild(gridCards);
    rowDisplayedItems.appendChild(divDisplayedItems);

    divUserItems.appendChild(rowSearchBar);
    divUserItems.appendChild(rowDisplayedItems);

    addSuggestionOnSearches(items);
    addSearchEventListener(user, items);
  } else {
    const divEmpty = document.createElement('div');
    divEmpty.className = 'mt-5';
    divEmpty.innerText = 'C\'est vide...';
    divUserItems.appendChild(divEmpty);
  }
}

function renderUserInformation(user) {
  const divUserInformation = document.getElementById('divSelectedOptMenu');
  divUserInformation.innerHTML = '';

  const rowUserInformation = document.createElement('div');
  rowUserInformation.className = 'row justify-content-between pt-4';

  const colUserInfo1 = document.createElement('div');
  colUserInfo1.className = 'col-4';

  const form = document.createElement('form');

  // Champ nom

  const divLastName = document.createElement('div');
  divLastName.className = 'input-group mb-3';

  const divLastNameField = document.createElement('div');
  divLastNameField.className = 'form-floating';

  const lastNameField = document.createElement('input');
  lastNameField.className = 'form-control';
  lastNameField.value = user.lastname;
  lastNameField.id = 'lastname';
  lastNameField.type = 'text';
  if (otherUser === true){
    lastNameField.disabled = true;
  }
  lastNameField.required = true;
  divLastNameField.appendChild(lastNameField);

  const labelLastName = document.createElement('label');
  labelLastName.className = 'form-label';
  labelLastName.innerText = 'Nom :';

  divLastNameField.appendChild(labelLastName);
  divLastName.appendChild(divLastNameField);

  // Champ prénom

  const divFirstName = document.createElement('div');
  divFirstName.className = 'input-group mb-3';

  const divFirstNameField = document.createElement('div');
  divFirstNameField.className = 'form-floating';

  const firstNameField = document.createElement('input');
  firstNameField.className = 'form-control';
  firstNameField.id = 'firstname';
  firstNameField.value = user.firstname;
  firstNameField.type = 'text';
  firstNameField.required = true;
  if (otherUser === true){
    firstNameField.disabled = true;
  }
  divFirstNameField.appendChild(firstNameField);

  const labelFirstName = document.createElement('label');
  labelFirstName.className = 'form-label';
  labelFirstName.innerText = 'Prénom :';

  divFirstNameField.appendChild(labelFirstName);
  divFirstName.appendChild(divFirstNameField);

  // Champ email

  const divEmail = document.createElement('div');
  divEmail.className = 'input-group mb-3';

  const divEmailField = document.createElement('div');
  divEmailField.className = 'form-floating';

  const emailField = document.createElement('input');
  emailField.className = 'form-control';
  emailField.id = 'email';
  emailField.value = user.email;
  emailField.type = 'email';
  emailField.required = true;
  if (otherUser === true){
    emailField.disabled = true;
  }
  divEmailField.appendChild(emailField);

  const labelEmail = document.createElement('label');
  labelEmail.className = 'form-label';
  labelEmail.innerText = 'Adresse mail :';

  divEmailField.appendChild(labelEmail);
  divEmail.appendChild(divEmailField);

  // Champ numéro de téléphone

  const divPhoneNumber = document.createElement('div');
  divPhoneNumber.className = 'input-group mb-3';

  const divPhoneNumberField = document.createElement('div');
  divPhoneNumberField.className = 'form-floating';

  const phoneNumberField = document.createElement('input');
  phoneNumberField.className = 'form-control';
  phoneNumberField.id = 'phoneNumber';
  phoneNumberField.value = user.phoneNumber;
  if (otherUser === true){
    phoneNumberField.disabled = true;
  }
  phoneNumberField.required = true;
  divPhoneNumberField.appendChild(phoneNumberField);

  const labelPhoneNumber = document.createElement('label');
  labelPhoneNumber.className = 'form-label';
  labelPhoneNumber.innerText = 'Numéro de téléphone :';

  divPhoneNumberField.appendChild(labelPhoneNumber);
  divPhoneNumber.appendChild(divPhoneNumberField);

  // Bouton Mettre à jour

  const editButton = document.createElement('button');
  editButton.className = 'btn text-white mt-5';
  editButton.innerText = 'Mettre à jour';
  editButton.id = 'fontColor';
  editButton.type = 'submit';

  const divErrorMsg = document.createElement('div');
  divErrorMsg.className = 'mt-2 text-danger';

  const versionNumber = document.createElement('input');
  versionNumber.id = 'versionNumber';
  versionNumber.type = 'hidden';
  versionNumber.value = user.version;

  colUserInfo1.appendChild(divLastName);
  colUserInfo1.appendChild(divFirstName);
  colUserInfo1.appendChild(divEmail);
  colUserInfo1.appendChild(divPhoneNumber);
  colUserInfo1.appendChild(versionNumber);
  if (otherUser === false){
    colUserInfo1.appendChild(editButton);
    colUserInfo1.appendChild(divErrorMsg);
  }

  const colUserInfo2 = document.createElement('div');
  colUserInfo2.className = 'col-4';

  const divUserPhoto = document.createElement('div');

  const divPhotoTitle = document.createElement('div');
  divPhotoTitle.id = 'divPhotoTitle';
  divPhotoTitle.className = 'mb-3';
  divPhotoTitle.innerText = 'Photo de profil :';
  divUserPhoto.appendChild(divPhotoTitle);

  const userPhoto = document.createElement('img');
  userPhoto.className = 'img-thumbnail';
  userPhoto.id = 'userPhoto';
  userPhoto.src = user.profilePhoto.accessPath;
  userPhoto.width = 150;
  userPhoto.height = 150;
  divUserPhoto.appendChild(userPhoto);

  if (otherUser === false){
    const divSetButton = document.createElement('div');
    divSetButton.innerText = 'Modifier la photo de profil : ';
    divSetButton.className = 'mt-2 h-25';
  
  
    const setButton = document.createElement('button');
    setButton.className = 'btn';
    setButton.id = 'fontColor';
  
    const iconSetButton = document.createElement('span');
    iconSetButton.innerHTML = `<i class="fa-regular fa-pen-to-square" style="color: #ffffff;"></i>`;
    setButton.appendChild(iconSetButton);
  
    divSetButton.appendChild(setButton);
    divUserPhoto.appendChild(divSetButton);
  
    colUserInfo2.appendChild(divUserPhoto);
  
    rowUserInformation.appendChild(colUserInfo1);
    rowUserInformation.appendChild(colUserInfo2);
    form.appendChild(rowUserInformation);
    divUserInformation.appendChild(form);
  
    setButton.addEventListener('click', () => {
      divSetButton.innerHTML = '';
  
      const rowEditPhoto = document.createElement('div');
      rowEditPhoto.className = 'row h-25';
  
      const colEditPhoto = document.createElement('div');
      colEditPhoto.className = 'col-11 bg-white border p-2';
  
      const navChoicePhoto = document.createElement('ul');
      navChoicePhoto.className = 'nav nav-tabs bg-white';
  
      const navImportPhoto = document.createElement('li');
      navImportPhoto.className = 'nav-item';
      const navLinkImportPhoto = document.createElement('a');
      navLinkImportPhoto.className = 'nav-link active cursor-pointer';
      navLinkImportPhoto.innerText = 'Importer une photo';
      navImportPhoto.appendChild(navLinkImportPhoto);
      navChoicePhoto.appendChild(navImportPhoto);
  
      const navAvatar = document.createElement('li');
      navAvatar.className = 'nav-item';
      const navLinkAvatar = document.createElement('a');
      navLinkAvatar.className = 'nav-link cursor-pointer';
      navLinkAvatar.innerText = 'Choisir un avatar';
      navAvatar.appendChild(navLinkAvatar);
      navChoicePhoto.appendChild(navAvatar);
  
      const divChosenOpt = document.createElement('div');
      divChosenOpt.id = 'divChosenOpt';
  
      colEditPhoto.appendChild(navChoicePhoto);
      colEditPhoto.appendChild(divChosenOpt);
      rowEditPhoto.appendChild(colEditPhoto);
      divSetButton.appendChild(rowEditPhoto);
  
      renderImportPhoto();
      navImportPhoto.addEventListener('click', () => {
        navLinkImportPhoto.className = 'nav-link active cursor-pointer';
        navLinkAvatar.className = 'nav-link cursor-pointer';
        divPhotoTitle.innerText = 'Photo de profil :';
        divPhotoTitle.removeAttribute('value');
        userPhoto.src = user.profilePhoto.accessPath;
        renderImportPhoto();
      });
      navAvatar.addEventListener('click', () => {
        navLinkImportPhoto.className = 'nav-link cursor-pointer';
        navLinkAvatar.className = 'nav-link active cursor-pointer';
        renderChooseAvatar();
      });
    });
  
    form.addEventListener('submit', async (e) => {
  
      const firstname = document.getElementById('firstname').value;
      const lastname = document.getElementById('lastname').value;
      const email = document.getElementById('email').value;
      const phone = document.getElementById('phoneNumber').value;
      const fileInput = document.getElementById('inputPhoto');
      const avatar = document.getElementById('divPhotoTitle').value;
      const version = document.getElementById('versionNumber').value;
      e.preventDefault();
      e.stopPropagation();
  
      if (
          firstname === user.firstname &&
          lastname === user.lastname &&
          email === user.email &&
          phone === user.phoneNumber &&
          fileInput == null &&
          avatar == null
      ) {
        divErrorMsg.innerText = "Erreur : aucun champ n'a été modifié";
      } else {
        const formData = new FormData();
  
        if (fileInput != null) {
          formData.append('file', fileInput.files[0]);
        } else if (avatar != null) {
          formData.append('avatarPath', avatar);
        }
  
        formData.append('firstname', firstname);
        formData.append('lastname', lastname);
        formData.append('email', email);
        formData.append('phone', phone);
        formData.append('version', version);
  
        await editUser(user.id, formData);
        window.location.reload();
      }
    });
  } else {
    colUserInfo2.appendChild(divUserPhoto);
  
    rowUserInformation.appendChild(colUserInfo1);
    rowUserInformation.appendChild(colUserInfo2);
    form.appendChild(rowUserInformation);
    divUserInformation.appendChild(form);
  }
}

function renderChangeUserPassword(user) {
  const divChangeUserPassword = document.getElementById('divSelectedOptMenu');
  divChangeUserPassword.innerHTML = '';

  const form = document.createElement('form');

  const rowOldPassword = document.createElement('div');
  rowOldPassword.className = 'row pt-4';

  const colOldPassword = document.createElement('div');
  colOldPassword.className = 'col-5';

  const divOldPassword = document.createElement('div');
  divOldPassword.className = 'input-group';

  const divOldPasswordField = document.createElement('div');
  divOldPasswordField.className = 'form-floating';

  const oldPasswordField = document.createElement('input');
  oldPasswordField.className = 'form-control';
  oldPasswordField.type = 'password';
  oldPasswordField.required = true;
  divOldPasswordField.appendChild(oldPasswordField);

  const labelOldPassword = document.createElement('label');
  labelOldPassword.className = 'form-label';
  labelOldPassword.innerText = 'Ancien mot de passe :';
  divOldPasswordField.appendChild(labelOldPassword);

  divOldPassword.appendChild(divOldPasswordField);
  colOldPassword.appendChild(divOldPassword);
  rowOldPassword.appendChild(colOldPassword);
  form.appendChild(rowOldPassword);

  //

  const rowNewPassword = document.createElement('div');
  rowNewPassword.className = 'row pt-3';

  const colNewPassword = document.createElement('div');
  colNewPassword.className = 'col-5';

  const divNewPassword = document.createElement('div');
  divNewPassword.className = 'input-group';

  const divNewPasswordField = document.createElement('div');
  divNewPasswordField.className = 'form-floating';

  const newPasswordField = document.createElement('input');
  newPasswordField.className = 'form-control';
  newPasswordField.type = 'password';
  newPasswordField.required = true;
  divNewPasswordField.appendChild(newPasswordField);

  const labelNewPassword = document.createElement('label');
  labelNewPassword.className = 'form-label';
  labelNewPassword.innerText = 'Nouveau mot de passe :';
  divNewPasswordField.appendChild(labelNewPassword);

  divNewPassword.appendChild(divNewPasswordField);
  colNewPassword.appendChild(divNewPassword);
  rowNewPassword.appendChild(colNewPassword);
  form.appendChild(rowNewPassword);

  //

  const rowConfirmPassword = document.createElement('div');
  rowConfirmPassword.className = 'row pt-3';

  const colConfirmPassword = document.createElement('div');
  colConfirmPassword.className = 'col-5';

  const divConfirmPassword = document.createElement('div');
  divConfirmPassword.className = 'input-group';

  const divConfirmPasswordField = document.createElement('div');
  divConfirmPasswordField.className = 'form-floating';

  const confirmPasswordField = document.createElement('input');
  confirmPasswordField.className = 'form-control';
  confirmPasswordField.type = 'password';
  confirmPasswordField.required = true;
  divConfirmPasswordField.appendChild(confirmPasswordField);

  const labelConfirmPassword = document.createElement('label');
  labelConfirmPassword.className = 'form-label';
  labelConfirmPassword.innerText = 'Confirmer votre nouveau mot de passe :';
  divConfirmPasswordField.appendChild(labelConfirmPassword);

  divConfirmPassword.appendChild(divConfirmPasswordField);
  colConfirmPassword.appendChild(divConfirmPassword);
  rowConfirmPassword.appendChild(colConfirmPassword);
  form.appendChild(rowConfirmPassword);

  const button = document.createElement('button');
  button.className = 'btn text-white mt-5';
  button.innerText = 'Mettre à jour';
  button.id = 'fontColor';
  button.type = 'submit';
  form.appendChild(button);
  divChangeUserPassword.appendChild(form);

  const divErrorMsg = document.createElement('div');
  divErrorMsg.className = 'mt-2 text-danger';
  divChangeUserPassword.appendChild(divErrorMsg);

  form.addEventListener('submit', (e) => {
    if (newPasswordField.value !== confirmPasswordField.value) {
      e.preventDefault();
      divErrorMsg.innerText = 'Erreur : les mots de passe ne correspondent pas.';
    } else {
      const oldPasswordValue = oldPasswordField.value;
      const newPasswordValue = newPasswordField.value;

      changePassword(user.id, oldPasswordValue, newPasswordValue);
    }
  });
}

function renderImportPhoto() {
  const divImportPhoto = document.getElementById('divChosenOpt');
  divImportPhoto.innerHTML = '';

  divImportPhoto.className = 'bg-white p-3';
  const inputPhoto = document.createElement('input');
  inputPhoto.className = 'form-control';
  inputPhoto.type = 'file';
  inputPhoto.id = 'inputPhoto';
  divImportPhoto.appendChild(inputPhoto);
}

async function renderChooseAvatar() {

  const avatars = await getAvatars();

  const divAvatar = document.getElementById('divChosenOpt');
  divAvatar.innerHTML = '';

  const userPhoto = document.getElementById('userPhoto');
  const divPhotoTitle = document.getElementById('divPhotoTitle');

  divAvatar.className = 'bg-white p-3 h-50';
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
      userPhoto.src = avatar.accessPath;
      divPhotoTitle.innerText = 'Photo de profil (avatar choisi):';
      divPhotoTitle.value = avatar.accessPath;
    });
  });
  divAvatar.appendChild(divDisplayedAvatars);
}

function addSuggestionOnSearches(items) {
  const homepageSearch = document.querySelector('.homepage-search');
  const searchInput = document.querySelector('.search-input');
  const suggestionsBox = document.querySelector('.suggestions-box');
  searchInput.addEventListener('keyup', (e) => {
    const input = searchInput.value;
    if (input && e.key !== 'Enter') {
      let suggestionList = [...items].filter((item) =>
          item.name.toLocaleLowerCase().includes(input.toLocaleLowerCase()));
      suggestionList = suggestionList.map((item) => `<li>${item.name}</li>`);
      const suggestionSet = [...new Set(suggestionList)];
      suggestionsBox.innerHTML = suggestionSet.join('');
      if (suggestionList.length !== 0) {
        homepageSearch.classList.add('active');
        const allLi = document.querySelectorAll('.homepage-search li');
        allLi.forEach((li) => {
          li.addEventListener('click', () => {
            searchInput.value = li.textContent;
            homepageSearch.classList.remove('active');
          })
        })
      } else {
        homepageSearch.classList.remove('active');
      }
    } else {
      homepageSearch.classList.remove('active');
    }
  })
}

function addSearchEventListener(user, items) {
  const searchInput = document.querySelector('.search-input');
  const searchIcon = document.querySelector('.homepage-search i');
  searchInput.addEventListener('keyup', (e) => {
    if (e.key === 'Enter') {
      readInputAndRenderItems(user, items, searchInput);
    }
  })
  searchIcon.addEventListener('click', () => {
    readInputAndRenderItems(user, items, searchInput);
  })
}

function readInputAndRenderItems(user, items, searchInput) {
  const input = searchInput.value;
  if (input) {
    const itemsFiltered = [...items].filter((item) =>
        item.name.toLocaleLowerCase().includes(input.toLocaleLowerCase()));
    renderUserItems(user, itemsFiltered);
  } else {
    renderUserItems(user, initialItems);
  }
}

export default ProfilePage;
