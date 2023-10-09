/* eslint-disable consistent-return */
/* eslint-disable no-use-before-define */
/* eslint-disable no-unused-vars */
import { right } from '@popperjs/core';
import { acceptOfferedItem, denyOfferedItem, getAllOfferedItems } from '../../models/items';
import { getAuthenticatedUser, isAuthenticated } from '../../utils/auths';
import {clearPage, renderLoading} from '../../utils/render';
import Navigate from '../Router/Navigate';
import {
  addZeroInFrontIfUnder2Digits
} from "../../utils/utils";

let chosenItem;

const OfferItemPage = async () => {
  renderLoading();
  const user = await getAuthenticatedUser()
  clearPage()
  if (isAuthenticated() && user.role === "admin") {
    clearPage();
    const main = document.querySelector('main');
    chosenItem = null;
    main.innerHTML = `
    <section class="offered-items-section">
      <h3 class="text-center p-2">Propositions d'objets</h3>
    </section>
    `;
    await renderTableOfferItems();
  }
  else{
    Navigate('/');
  }
};

const clearRightTab = () => {
  const rightTab = document.querySelector('.rightTab');
  rightTab.innerHTML = '';
};

function renderOfferedItems(allOfferedItems){
  let allThItems = '';
  allOfferedItems?.forEach((item) => {
      allThItems += `
      <tr>
      <td class="offeredItem text-center text-primary" id="item_${item.id}">${item.name}</td>
      <td class="text-center" id="item_${item.id}">${item.type}</td>
      <td class="text-center" id="item_${item.id}">${item.description}</td>
      <td class="text-center" id="item_${item.id}">
          ${addZeroInFrontIfUnder2Digits(item.meetingDate.date[2])}-${addZeroInFrontIfUnder2Digits(item.meetingDate.date[1])}-${addZeroInFrontIfUnder2Digits(item.meetingDate.date[0])}
      </td>
      <td class="text-center" id="item_${item.id}"><img src=${item.photo.accessPath} height ="120" width = "180"></td>
      </tr>
      `
  });

  
  return allThItems;
}

function attachOnClickEventsToRenderItem(allOfferedItems) {
  allOfferedItems?.forEach((item) => {
    const currentItem = document.getElementById(`item_${item.id}`);
    currentItem.addEventListener('click', () => {
      Navigate('/item?id=', item.id);
    });
  });
  
}
async function renderTableOfferItems(){
    const section = document.querySelector('.offered-items-section');
    const wrapperTableAndBtn = document.createElement('div');
    wrapperTableAndBtn.className = "allWrapper bg-white border rounded col-md-10 offset-md-1";

    const div1 = document.createElement('div')
    div1.className = 'container d-flex flex-column mt-5 TableOfferItems';
    const tableOfferItems = document.createElement('table');

    tableOfferItems.classList.add('table', 'table-bordered', "bg-white");
    const thead = document.createElement('thead');

    const tr1 = document.createElement('tr');

    const th1 = document.createElement('th');
    const th2 = document.createElement('th');
    const th3 = document.createElement('th');
    const th4 = document.createElement('th');
    const th5 = document.createElement('th');

    th1.textContent = 'Nom';
    th2.textContent = 'Type';
    th3.textContent = 'Description';
    th4.textContent = 'Date de visite';
    th5.textContent = 'Photo';
    thead.className = 'table-active text-center'


    tr1.appendChild(th1);
    tr1.appendChild(th2);
    tr1.appendChild(th3);
    tr1.appendChild(th4);
    tr1.appendChild(th5);
    thead.appendChild(tr1); 

    tableOfferItems.appendChild(thead);

    // get offered items 
    const listOfferedItems = await getAllOfferedItems();
  
    tableOfferItems.innerHTML += renderOfferedItems(listOfferedItems);

    div1.appendChild(tableOfferItems);
    
    wrapperTableAndBtn.appendChild(div1);
    section.appendChild(wrapperTableAndBtn);
    attachOnClickEventsToRenderItem(listOfferedItems);

}

export default OfferItemPage;
