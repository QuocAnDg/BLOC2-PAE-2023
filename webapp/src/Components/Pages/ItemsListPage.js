import {readAllItems} from "../../models/items";
import Navigate from "../Router/Navigate";
import {
  itemStateFrenchTranslation
} from "../../utils/translator";

import addZeroInFrontIfUnder2Digits from "../../utils/utils";
import {getAuthenticatedUser, isAuthenticated} from "../../utils/auths";
import { clearPage } from "../../utils/render";

const ItemsListPage = async () => {
  clearPage();
  const user = await getAuthenticatedUser();
  const main = document.querySelector('main');
  if (isAuthenticated() && (user.role === "admin" || user.role === "helper")) {
    const items = await readAllItems();

    main.innerHTML = `
  <section>
    <div>
      <h5 class="text-center">Voici la liste des objets</h5>
    </div>
    <div class="d-flex justify-content-center">
      <table class="table table-bordered items-table mx-5">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Type</th>
            <th>Description</th>
            <th>Date de réception</th>
            <th>Etat</th>
            <th>Photo</th>
          </tr>
        </thead>
        <tbody class="table-body">
        
        </tbody>
      </table>
    </div>
  </section>
  `;

    const tableBody = document.querySelector(".table-body");
    items.forEach(item => {
      const row = document.createElement("tr");
      const nameElement = document.createElement("td");
      nameElement.classList.add(`item-name-${item.id}`, 'item-name-element');
      const typeElement = document.createElement("td");
      const descriptionElement = document.createElement("td");
      const meetingDateElement = document.createElement("td");
      const stateElement = document.createElement("td");
      const photoElement = document.createElement("td");

      nameElement.innerText = `${item.name}`;
      typeElement.innerText = `${item.type}`;
      descriptionElement.innerText = `${item.description}`;
      stateElement.innerText = `${itemStateFrenchTranslation(item.state)}`;
      meetingDateElement.innerText = `${addZeroInFrontIfUnder2Digits(item.meetingDate.date[2])}-${addZeroInFrontIfUnder2Digits(item.meetingDate.date[1])}-${item.meetingDate.date[0]}`;
      photoElement.innerHTML = `${item.photo.accessPath ? `<img src=${item.photo.accessPath} width="50" height="50">` : "Pas de photo"}`;


      row.appendChild(nameElement);
      row.appendChild(typeElement);
      row.appendChild(descriptionElement);
      row.appendChild(meetingDateElement);
      row.appendChild(stateElement);
      row.appendChild(photoElement);

      tableBody.appendChild(row);

      nameElement.addEventListener('click', (e) => {
        e.preventDefault();
        Navigate('/item?id=', item.id);
      })
    })
  }
  else{
    main.innerHTML = `<h1>Accès non autorisé</h1>`;
  }

};


export default ItemsListPage;