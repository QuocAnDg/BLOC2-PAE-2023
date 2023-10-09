import {
  acceptOfferedItem,
  denyOfferedItem,
  getAllItemTypes,
  readAllItemsFromUser,
  readOneItem,
  updateState,
  updateStateForSale,
  updateItemInformation,
  updatePhoto,
  updateStateSoldFromInStore
} from "../../models/items";
import {
  itemStateFrenchTranslation,
  itemTimeSlotFrenchTranslation
} from "../../utils/translator";
import {
  addZeroInFrontIfUnder2Digits, replaceDotWithComma
} from "../../utils/utils";
import {clearPage, renderItemsTable, renderLoading} from "../../utils/render";
import Navigate from "../Router/Navigate";
import {getAuthenticatedUser, isAuthenticated} from "../../utils/auths";

const ItemPage = async () => {
  clearPage();
  renderLoading()
  const user = await getAuthenticatedUser();
  clearPage()
  const main = document.querySelector('main');
  if (isAuthenticated() && (user.role === "admin" || user.role === "helper")){
    renderLoading()
    const item = await readOneItem();
    clearPage()

    main.innerHTML = `
 
  <div class="container-fluid wrapperAdmin">
    <div id="item-details-wrapper"></div>
  </div>
  `;

    // <div class="row border">
    //   <div class="col-lg-2 col-md-4 p-0">
    //     <div class="btn-group-toggle btn-group-vertical w-100 text-center list-group list-group-flush leftTab border border-dark">
    //       <button class="btn info-btn list-group-item list-group-item-action adminBtn active">Informations objet</button>
    //       ${item.state === 'proposed' ? `<button class="btn action-btn list-group-item list-group-item-action adminBtn">Actions proposition</button>` : ""}
    //     </div>
    //   </div>
    //
    //   <div class="col-lg-10 col-md-8 col-xs-12 current-tab border border-dark d-flex justify-content-between">
    //   </div>
    // </div>

    await renderItemPage(item);
    await renderOwnersItems(item);

    // renderItemInformation(item);
  }
  else{
    main.innerHTML = `<h1>Accès non autorisé</h1>`;
  }
  main.classList.add("no-scrollbar");
};

async function renderItemPage(item) {
  const detailsWrapper = document.getElementById('item-details-wrapper');
  detailsWrapper.className = 'p-3 mt-3 border rounded';
  detailsWrapper.innerHTML = '';

  const details = document.createElement('div');
  details.className = 'd-flex flex-row';

  const authenticatedUser = await getAuthenticatedUser();

  const detailsPills = document.createElement('div');
  detailsPills.className = 'nav flex-column nav-pills pe-3 me-3 border-end';
  detailsPills.role = 'tablist';
  detailsPills.ariaOrientation = 'vertical';
  detailsPills.innerHTML = `
      <button class="nav-link active item-info-btn" id="v-pills-info-tab" data-bs-toggle="pill" data-bs-target="#v-pills-info" type="button" role="tab" aria-controls="v-pills-home" aria-selected="true" >
          Informations objet
      </button>
      <button class="nav-link owner-info-btn" id="v-pills-owner-tab" data-bs-toggle="pill" data-bs-target="#v-pills-owner" type="button" role="tab" aria-controls="v-pills-disabled" aria-selected="false" >
          Informations propriétaire
      </button> 
      ${item.state === 'proposed' || item.state === 'denied' ? "" :
      `<button class="nav-link action-btn" id="v-pills-actions-tab" data-bs-toggle="pill" data-bs-target="#v-pills-actions" type="button" role="tab" aria-controls="v-pills-disabled" aria-selected="false">
          Actions
      </button>`} 
      ${ (authenticatedUser.role === 'admin' && (item.state === 'proposed' || item.state === 'in_store'))  ?
      `<button class="nav-link admin-action-btn" id="v-pills-admin-actions-tab" data-bs-toggle="pill" data-bs-target="#v-pills-admin-actions" type="button" role="tab" aria-controls="v-pills-disabled" aria-selected="false">
          Actions du responsable
      </button>`
      : ""
  } 
      
  `;
  details.appendChild(detailsPills);

  const detailsTabs = document.createElement('div');
  detailsTabs.className = 'tab-content flex-grow-1';

  const infoTab = document.createElement('div');
  infoTab.className = 'tab-pane fade show active';
  infoTab.id = 'v-pills-info';
  infoTab.role = 'tabpanel';
  infoTab.ariaLabel = 'v-pills-info-tab';
  infoTab.tabIndex = '0';

  let itemDropdown = '';
  const allItemsTypes = await getAllItemTypes();
  allItemsTypes.forEach((itemType) => {
    if(itemType === item.type){
      itemDropdown += `<option selected="selected" value="${itemType}">${itemType}</option>`
    }
    else{
      itemDropdown += `<option value="${itemType}">${itemType}</option>`
    }
  });

  infoTab.innerHTML = `
  <div class="d-flex justify-content-between">
    <div class="divLabelLeft">
      <div class="nameDiv my-3">
        <p class="labelName d-inline">Nom :</p>
        <p class="infoName d-inline">${item.name}</p>
      </div>
      <div class="typeDiv mb-3">
          <label for="itemType">Type de l'objet:</label>
          <select name="itemType" id="itemType">
            ${itemDropdown};
          </select> 
      </div>
      <div class="dateDiv mb-3">
        <p class="labelDate d-inline">Date de réception :</p>
        <p class="infoDate d-inline">
        ${addZeroInFrontIfUnder2Digits(item.meetingDate.date[2])}-${addZeroInFrontIfUnder2Digits(item.meetingDate.date[1])}-${item.meetingDate.date[0]}
        (${itemTimeSlotFrenchTranslation(item.state)})
        </p>
      </div>
      <div class="descriptionDiv mb-3">
        <p class="labelDescription d-inline">Description : </p>
        <input type="text" id="itemDescription" value="${item.description}">
        <div id="errorMsgDescription"></div>
      </div>
      <div class="stateDiv mb-3">
        <p class="labelState d-inline">Etat : </p>
        <p class="infoState d-inline">${itemStateFrenchTranslation(item.state)}</p>
      </div>
      <div class="priceDiv mb-3">
        <p class="labelPrice d-inline">Prix : </p>
        <p class="infoPrice d-inline">${item.price ? `${replaceDotWithComma(item.price)} €`  : `Pas de prix`}</p>
      </div>
      <input type="hidden" id="itemVersion" value="${item.version}">
      <div>
        <button type="button" class="btn btn-outline-primary" id="modify-item-information">Modifier informations</button>
      </div>
    </div>
     
    <div class="divPhotoRight">
      <div class="divPhoto  d-inline">
        <p class="labelPhoto">Photo</p>
        <img class="imgItem" height="100" width="100" src=${item.photo.accessPath ? item.photo.accessPath : "Aucune photo"} alt="">
        <form>
          <label>Choisir une nouvelle photo</label>
          <input class="form-control" name="file" type= "file" /> <br/><br/>
          <button type="button" class="btn btn-outline-primary" id="modify-item-photo">Modifier la photo</button>
        </form>
      </div>
    </div>
  </div>
  
  `;
  detailsTabs.appendChild(infoTab);


  const adminActionsTab = document.createElement('div');
  adminActionsTab.className = 'tab-pane fade';
  adminActionsTab.id = 'v-pills-admin-actions';
  adminActionsTab.role = 'tabpanel';
  adminActionsTab.ariaLabel = 'v-pills-admin-actions-tab';
  adminActionsTab.tabIndex = '0';
  const adminActionsDiv = document.createElement('div');
  adminActionsTab.appendChild(adminActionsDiv);

  if (item.state === 'proposed') {
    const topTab = document.createElement('div');
    const bottomTab = document.createElement('div');
    bottomTab.className = 'd-flex flex-column';
    const acceptOfferText = document.createElement('h5');
    acceptOfferText.textContent = 'Accepter la proposition : ';
    acceptOfferText.className = 'd-inline';
    const acceptOfferBtn = document.createElement('input');
    acceptOfferBtn.value = 'Accepter';
    acceptOfferBtn.type = 'submit';
    acceptOfferBtn.className = 'btn text-white';
    acceptOfferBtn.id = 'fontColor';

    const errorMsg = document.createElement('p');
    errorMsg.id = "errorMsg";
    errorMsg.className = "text-danger"
    errorMsg.textContent = "";

    const orText = document.createElement('h4');
    orText.textContent = 'Ou'
    orText.className = 'd-flex justify-content-center'

    const denyOfferText = document.createElement('h5');
    denyOfferText.textContent = 'En cas de refus, joindre un petit mot explicatif :'
    const divDenyBtn = document.createElement('div');
    divDenyBtn.className = 'd-flex justify-content-end w-10';
    const denyOfferBtn = document.createElement('input');
    denyOfferBtn.value = 'Refuser';
    denyOfferBtn.type = 'submit';
    denyOfferBtn.className = 'btn text-white p-2 w-25';

    denyOfferBtn.id = 'fontColor';
    const explicationDeny = document.createElement('textarea');

    explicationDeny.rows = "5";
    explicationDeny.cols = "100";
    explicationDeny.className = 'form-control p-2';

    divDenyBtn.appendChild(denyOfferBtn);

    topTab.appendChild(acceptOfferText);
    bottomTab.appendChild(denyOfferText);
    bottomTab.appendChild(explicationDeny);
    denyOfferBtn.addEventListener('click', async () =>{

      if (explicationDeny.value === null || explicationDeny.value === "") {

        document.querySelector('#errorMsg').textContent = "Le champ d'explication de refus n'a pas été rempli";


      }
      else{
        await denyOfferedItem(explicationDeny.value, item.id);
        clearPage();
        Navigate('consultOfferItems');
      }

    });

    bottomTab.appendChild(errorMsg);
    bottomTab.appendChild(divDenyBtn);

    acceptOfferText.appendChild(acceptOfferBtn);

    adminActionsDiv.appendChild(topTab);
    adminActionsDiv.appendChild(orText);
    adminActionsDiv.appendChild(bottomTab);
    acceptOfferBtn.addEventListener('click', async () =>{
      await acceptOfferedItem(item.id);
      clearPage();
      Navigate('consultOfferItems');

    });

  } else if (item.state === 'in_store') {
    const buttonsWrapper = document.createElement('div');
    buttonsWrapper.className = 'd-flex flex-column justify-content-evenly align-items-center h-100';

    const inputWrapper = document.createElement('div');
    inputWrapper.className = 'ms-5 col-3 align-self-start';

    const label = document.createElement('label');
    label.className = 'form-label text-nowrap';
    label.innerText = "Veuillez entrer le prix auquel l'objet a été vendu :";
    label.htmlFor = 'price';
    inputWrapper.appendChild(label);

    const inputGroup = document.createElement('div');
    inputGroup.className = 'input-group col-lg-12';
    const input = document.createElement('input');
    input.id = 'price';
    input.className = 'form-control';
    input.type = 'number';
    input.min = '0';
    input.step = '0.01';
    input.placeholder = 'Prix de vente';
    const inputGroupText = document.createElement('span');
    inputGroupText.className = 'input-group-text';
    inputGroupText.innerText = '€';
    inputGroup.appendChild(input);
    inputGroup.appendChild(inputGroupText);

    inputWrapper.appendChild(inputGroup);
    buttonsWrapper.appendChild(inputWrapper);

    const btn = document.createElement('button');
    btn.className = 'btn btn-primary col-3 align-self-end me-5';
    btn.innerText = 'Objet vendu';
    btn.addEventListener('click', async () => {
      await updateStateSoldFromInStore(item.id, input.value);
      Navigate('/item?id=', item.id);
    });
    buttonsWrapper.appendChild(btn);
    adminActionsDiv.appendChild(buttonsWrapper);
  }



  detailsTabs.appendChild(adminActionsTab);

  const ownerTab = document.createElement('div');
  ownerTab.className = 'tab-pane fade';
  ownerTab.id = 'v-pills-owner';
  ownerTab.role = 'tabpanel';
  ownerTab.ariaLabel = 'v-pills-owner-tab';
  ownerTab.tabIndex = '0';
  ownerTab.innerHTML= `
  <div class="d-flex justify-content-between">
    <div class="divLabelLeft">
      ${!item.offeringMember 
      ? 
      `<div class="phoneNumberDiv my-3">
        <p class="labelPhoneNumber d-inline">GSM :</p>
        <p class="infoName d-inline">${item.phoneNumber}</p>
       </div>`
      : 
      `<div class="ownerLastnameDiv my-3">
        <p class="labelOwnerLastname d-inline">Nom :</p>
        <p class="ownerLastname d-inline">${item.offeringMember.lastname}</p>
      </div>
      <div class="ownerFirstnameDiv mb-3">
        <p class="labelOwnerFirstname d-inline">Prénom :</p>
        <p class="ownerFirstname d-inline">${item.offeringMember.firstname}</p>
      </div>
      <div class="ownerEmailDiv mb-3">
        <p class="labelOwnerEmail d-inline">Email :</p>
        <p class="ownerEmail d-inline">${item.offeringMember.email}</p>
      </div>
      <div class="ownerPhoneNumberDiv mb-3">
        <p class="labelOwnerPhoneNumber d-inline">GSM :</p>
        <p class="ownerPhoneNumber d-inline">${item.offeringMember.phoneNumber}</p>
      </div>
    </div>
     
    <div class="divPhotoRight">
      <div class="divPhoto  d-inline">
        <p class="labelPhoto">Photo</p>
        <img class="imgItem" height="100" width="100" src=${item.offeringMember.profilePhoto.accessPath} alt="">
      </div>
    </div>
    `}
  </div>
  ${item.offeringMember
      ?
      `
      <div id="table-recherche-wrapper" class="container overflow-auto border mt-3 p-3 pb-0 bg-light text-center">
        <table class="table table-bordered table-hover bg-white">
          <thead>
            <tr>
              <th class="col">Nom</th>
              <th class="col">Type</th>
              <th class="col-3">Description</th>
              <th class="col">Date de réception</th>
              <th class="col">État</th>
              <th class="col">Photo</th>
            </tr>
          </thead>
          <tbody class="owners-items-table-body">
          </tbody>
        </table>
      </div>
      `:
      ""}
  `;
  detailsTabs.appendChild(ownerTab);


  const actionsTab = document.createElement('div');
  actionsTab.className = 'tab-pane fade h-100';
  actionsTab.id = 'v-pills-actions';
  actionsTab.role = 'tabpanel';
  actionsTab.ariaLabel = 'v-pills-actions-tab';
  actionsTab.tabIndex = '0';

  const buttonsWrapper = document.createElement('div');
  buttonsWrapper.className = 'd-flex flex-column justify-content-evenly align-items-center h-100';

  const buttons = document.createElement('div');
  buttons.className = 'd-flex justify-content-evenly w-100';

  if (item.state === 'confirmed' || item.state === 'in_workshop') {

    if (item.state === 'confirmed') {
      const btn = document.createElement('button');
      btn.className = 'btn btn-primary col-3';
      btn.innerText = 'Objet déposé à l\'atelier';
      btn.addEventListener('click', async () => {
        await updateState(item.id, 'in_workshop');
        Navigate('/itemslist');
      });
      buttons.appendChild(btn);
    }

    const btn = document.createElement('button');
    btn.className = 'btn btn-primary col-3';
    btn.innerText = 'Objet déposé au magasin';
    btn.addEventListener('click', async () => {
      await updateState(item.id, 'in_store');
      Navigate('/item?id=', item.id);
    });
    buttons.appendChild(btn);
  }

  if (item.state === 'in_store') {
    const inputWrapper = document.createElement('div');
    inputWrapper.className = 'ms-5 col-3 align-self-start';

    const label = document.createElement('label');
    label.className = 'form-label';
    label.innerText = 'Veuillez entrer un prix de vente :';
    label.htmlFor = 'price';
    inputWrapper.appendChild(label);

    const inputGroup = document.createElement('div');
    inputGroup.className = 'input-group';
    const input = document.createElement('input');
    input.id = 'price';
    input.className = 'form-control';
    input.type = 'number';
    input.min = '0';
    input.step = '0.01';
    input.placeholder = 'Prix de vente';
    const inputGroupText = document.createElement('span');
    inputGroupText.className = 'input-group-text';
    inputGroupText.innerText = '€';
    inputGroup.appendChild(input);
    inputGroup.appendChild(inputGroupText);

    inputWrapper.appendChild(inputGroup);
    buttonsWrapper.appendChild(inputWrapper);

    const btn = document.createElement('button');
    btn.className = 'btn btn-primary col-3 align-self-end me-5';
    btn.innerText = 'Mettre en vente';
    btn.addEventListener('click', async () => {
      await updateStateForSale(item.id, input.value);
      Navigate('/item?id=', item.id);
    });
    buttonsWrapper.appendChild(btn);
  }

  if (item.state === 'for_sale') {
    const soldBtn = document.createElement('button');
    soldBtn.className = 'btn btn-primary col-3';
    soldBtn.innerText = 'Objet vendu';
    soldBtn.addEventListener('click', async () => {
      await updateState(item.id, 'sold');
      Navigate('/item?id=', item.id);
    });
    buttons.appendChild(soldBtn);

    const removedBtn = document.createElement('button');
    removedBtn.className = 'btn btn-primary col-3';
    removedBtn.innerText = 'Objet retiré de la vente';
    removedBtn.addEventListener('click', async () => {
      await updateState(item.id, 'removed');
      Navigate('/item?id=', item.id);
    });
    buttons.appendChild(removedBtn);
  }

  buttonsWrapper.appendChild(buttons);
  actionsTab.appendChild(buttonsWrapper);
  detailsTabs.appendChild(actionsTab);

  details.appendChild(detailsTabs);
  detailsWrapper.appendChild(details);

  document.getElementById("modify-item-information").addEventListener('click', async () => {
    const description = document.getElementById("itemDescription").value;
    const type = document.getElementById("itemType").value;
    const version = document.getElementById("itemVersion").value;
    if(description.value === null || description.value === ""){
      document.getElementById("errorMsgDescription").innerText = "Entrez une description";
    }
    else{
      await updateItemInformation(item.id, description, type, version);
      Navigate('/item?id=', item.id);
    }
  })

  document.getElementById("modify-item-photo").addEventListener('click', async () => {
    const fileInput = document.querySelector('input[name=file]');
    await updatePhoto(item.id, fileInput);
    Navigate('/item?id=', item.id);
  })

}

async function renderOwnersItems(item) {
  if (item.offeringMember) {
    const userItems = await readAllItemsFromUser(item.offeringMember.id);
    const ownersItemsTableBody = document.querySelector('.owners-items-table-body');
    userItems.forEach(ligne => {
      const tr = document.createElement('tr');
      tr.classList.add("cursor-pointer");
      tr.addEventListener('click', (e) => {
        e.preventDefault();
        Navigate('/item?id=', item.id);
      })
      // tr.id = 'item-'.concat(ligne.id);
      renderItemsTable(tr, ligne);
      ownersItemsTableBody.appendChild(tr);
    })
  }
}



export default ItemPage;