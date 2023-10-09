/* eslint-disable func-names */
/* eslint-disable no-use-before-define */
/* eslint-disable no-unused-vars */
// eslint-disable-next-line import/no-extraneous-dependencies
import flatpickr from 'flatpickr';
import { getAllItemTypes } from '../../models/items';
import { getAllAvailabilitiesDate } from '../../models/availabilities';
import {
  getAuthenticatedUser,
  getToken,
  isAuthenticated,
  setAuthenticatedUser
} from '../../utils/auths';
import {clearPage, renderLoading} from '../../utils/render';
import Navigate from '../Router/Navigate';
import Navbar from '../Navbar/Navbar';
import {
  addZeroInFrontIfUnder2Digits
} from '../../utils/utils';
import { itemStateFrenchTranslation, itemTimeSlotFrenchTranslation } from '../../utils/translator';

let phoneNumber;
let photoUpload;
const OfferItemPage = async () => {
  clearPage();
  await getAuthenticatedUser();
  if (isAuthenticated()){
    await renderOfferPage();
  }
  else{
    renderPreOfferPage();
  }

};
async function addOfferedItem(e){
  e.stopPropagation();
  e.preventDefault();

  const availibitiyDate = document.getElementById("datepicker").value;
  const daytime = document.getElementById("daytime");
  const evening = document.getElementById("afternoon");
  const itemName = document.getElementById("itemName").value;
  const itemType = document.getElementById("typeItem").value;
  const description = document.getElementById("description").value;
  const fileInput = document.querySelector("input[name=file]");

  const formData = new FormData();
  formData.append("file", fileInput.files[0]);
  formData.append("name", itemName);
  formData.append("type", itemType);
  formData.append("description", description);
  if (!isAuthenticated()){
    formData.append("phoneNumber", phoneNumber);
  }
  formData.append("meetingDate", availibitiyDate);
  if (daytime.checked){
    formData.append("timeSlot",daytime.value);
  }
  else{
    formData.append("timeSlot",evening.value);
  }
  if (isAuthenticated()){
    const options = {
      method: 'POST',
      body: formData,
      headers:{
        'Authorization': getToken(),
      }
    };
    const response = await fetch(`${process.env.API_BASE_URL}/items/offerItemUser`, options)
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  }
  else{
    const options = {
      method: 'POST',
      body: formData,
    };
    const response = await fetch(`${process.env.API_BASE_URL}/items/offerItem`, options)
  
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  }
  clearPage();
  Navbar();
  renderInfosItem(itemName,itemType, availibitiyDate, description, photoUpload, daytime.checked ? daytime.value : evening.value);
}

async function renderOfferPage(){
  const main = document.querySelector('main');
  renderLoading()
  const allAvailabilitiesDate = await getAllAvailabilitiesDate();
  clearPage();
  const dateStringList = allAvailabilitiesDate.map(date => date.join('-')) ; 

  main.innerHTML = `
    <section class = "preOffer">
      <div class = "p-3">
        <div class = "container d-flex flex-column bg-white border rounded mt-5 p-4">
          <form id = "formOffer">
          <div class="row"><h2 class="mb-3">Proposition d'objet</h2></div>
           <div class="row">
              <div class = "col">
               

                <label>Choisir un samedi</label>
                <input class = "form-control mb-3" type="datetime-local" id="datepicker" placeholder="Sélectionner une date" required>
               
                <label for="daytime">Choisir un plage horaire</label>
                <div class = "plageHoraire p-2">
                  <input type="radio" name="timeslot" id="daytime" value="daytime" required="required">
                  <label for="daytime" class="me-3">Matinée</label>
                  <input type="radio" name="timeslot" id="afternoon" value="evening">
                  <label for="evening">Après-midi</label>
                </div>

                <label>Entrez un nom d'objet</label>
                <input class = "form-control mb-3" type="text" placeholder="nom" id = "itemName" required>

                <div>
                  <label>Choisir un type d'objet</label>
                  <select name="types" id="typeItem" class = "form-select mb-3" required>
                  ${await optionsItemTypes()}
                  </select>
                </div>

                <label>Description (Max. 120 caractères)</label>
                <textarea class="form-control mb-3" rows="3" maxlength = "120" placeholder = "description" id = "description" required></textarea>
                
                <input type = "submit" value = "Soumettre" class = "btn text-white mb-3" id = "fontColor">
             
              </div>
          
              <div class = "col">
                <div id="photo-upload-form">
                    <label id = "preOffer">Choisir une photo</label>
                    <div>
                      <img id = "imageUpload" height="120" width="200">
                      </div>
                    <input class="form-control mt-2" name="file" type="file" id = "filename" required/>
                </div>
              </div>
        
            </div>
            </form>
        </div>
      </div>
    </section>
  `;
  flatpickr("input[type=datetime-local]",{
    dateFormat: "Y-m-d",  
    enable : dateStringList,
  });
  renderImageInputFile();
  const form = document.getElementById("formOffer");
  form.addEventListener("submit", addOfferedItem);
}
function renderImageInputFile(){
  const fileInput = document.querySelector("input[name=file]");
  const img = document.querySelector("#imageUpload");
  fileInput.addEventListener("change", () =>{
    const reader = new FileReader();
    reader.addEventListener("load", () =>{
      img.src = reader.result;
      photoUpload = reader.result;
    })
    reader.readAsDataURL(fileInput.files[0]);
  });
}

function renderPreOfferPage(){
  const main = document.querySelector('main');
  
  main.innerHTML = `
  <section class = "preOffer">
      <div class = "p-3">
          <div class = "container d-flex flex-column bg-white border rounded mt-5 p-4">
              <h3>Pour continuer</h3>
              <h4>Identifiez-vous avec un compte :</h4>
              <div class = "mb-2 w-50 form-check mx-auto text-center">
                  <form id = "loginPreOffer">
                  <div class = "input-group px-5">
                      <span class = "input-group-text mb-3"><i class="fa fa-solid fa-envelope"></i></span>
                      <input class = "form-control mb-3" type = "text" placeholder = "Email" id = "email" data-error="Veuillez entrez un email">
                  </div>
                  <div class = "input-group px-5">
                      <span class = "input-group-text mb-3"><i class="fa fa-solid fa-lock"></i></span>
                      <input class = "form-control mb-3" type = "password" placeholder = "Mot de passe" id = "password">
                  </div>
                  <input type = "submit" value = "Se connecter et continuer" class = "btn text-white preLogin mb-3" id = "fontColor">
                  <br>
                  <div class="row d-inline-flex">
                    <p class="col-auto pe-0">Pas encore inscrit ? S'inscrire</p>
                    <p class="col-auto text-primary px-1" id="linkRegister" style="cursor: pointer;">ici</p>
                  </div>
                <div class="login-error-div">
                  <p class="login-error-p"></p>
                </div>
                </form>
                <h3>Ou</h3>
              </div>
              <h4>Introduire un numéro de gsm :</h4>
              <form id = "phoneNumberContinue">
                <div class = "mb-2 w-50 form-check mx-auto text-center">
                    <div class = "input-group px-5">
                        <span class = "input-group-text mb-3"><i class="fa fa-mobile"></i></span>
                        <input pattern= "^[0-9.\\/( )]*$" class = "form-control mb-3" 
                        type = "tel" placeholder = "Numéro de téléphone" id = "phoneNumber" required>
                    </div>
                    <input type = "submit" value = "Continuer" class = "btn text-white continuePhoneNumber" id = "fontColor">
                </div>
                </form>
          </div>
      </div>
  </section>
  `;

  const form = document.getElementById("loginPreOffer");
  form.addEventListener('submit', onLogin);

  const formPhoneNumber = document.getElementById("phoneNumberContinue");
  formPhoneNumber.addEventListener("submit", () =>{
    phoneNumber = document.getElementById ('phoneNumber').value;
    clearPage();
    renderOfferPage();
  });
  const linkRegister = document.querySelector('#linkRegister');
  linkRegister.addEventListener('click',() => {
      Navigate('/register');
    });
}
async function optionsItemTypes(){
  const listTypes = await getAllItemTypes();
  let optionsHTML = ``
  listTypes.forEach(typeName => {
    const optionTypeName = `<option value="${typeName}">${typeName}</option>`
    optionsHTML += optionTypeName;
  });
  return optionsHTML;
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

  clearPage();
  Navbar();
  renderOfferPage();
}

function renderInfosItem(name, type, meetingDate, description, photoName, timeSlot){
  const meetingDateInfos = meetingDate.split('-');
  const main = document.querySelector('main');

  const infoTab = document.createElement('div');
  infoTab.innerHTML = `
  <div class = "p-3">
    <div class = "container ml-2">
      <p class ="text-success">Proposition enregistrée</p>
      <p>Récapitulatif: </p>
    </div>
    <div class = "container d-flex flex-column bg-white border rounded mt-2">
      <div class="d-flex justify-content-between">
        <div class="divLabelLeft">
          <div class="nameDiv my-3">
            <p class="labelName d-inline">Nom :</p>
            <p class="infoName d-inline">${name}</p>
          </div>
          <div class="typeDiv mb-3">
            <p class="labelType d-inline">Type :</p>
            <p class="infoType d-inline">${type}</p>
          </div>
          <div class="dateDiv mb-3">
            <p class="labelDate d-inline">Date de réception :</p>
            <p class="infoDate d-inline">
            ${addZeroInFrontIfUnder2Digits(meetingDateInfos[2])}/${addZeroInFrontIfUnder2Digits(meetingDateInfos[1])}/${meetingDateInfos[0]}
            (${itemTimeSlotFrenchTranslation(timeSlot)})
            </p>
          </div>
          <div class="descriptionDiv mb-3">
            <p class="labelDescription d-inline">Description : </p>
            <p class="infoDescription d-inline">${description}</p>
          </div>
          <div class="stateDiv mb-3">
            <p class="labelState d-inline">Etat : </p>
            <p class="infoState d-inline">${itemStateFrenchTranslation("proposed")}</p>
          </div>
          <input type = "submit" value = "Retour vers la page principale" class = "btn text-white continueHomePage mb-3" id = "fontColor">
        </div>
        
        <div class="divPhotoRight">
          <div class="divPhoto  d-inline">
            <p class="labelPhoto">Photo:</p>
            <img class="imgItem" height="120" width="200" src=${photoName} alt="">
          </div>
        </div>
      </div>
    </div>
  </div>
  `;
  main.appendChild(infoTab);
  const continueHomePage = document.querySelector(".continueHomePage");
  continueHomePage.addEventListener('click', async () =>{
    clearPage();
    Navigate('/');
  });
}
export default OfferItemPage;
