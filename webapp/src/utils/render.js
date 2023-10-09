
import {
  addZeroInFrontIfUnder2Digits
} from "./utils";
import {itemStateFrenchTranslation} from "./translator";

const clearPage = () => {
  const main = document.querySelector('main');
  main.innerHTML = '';
  main.className = '';
};

const renderPageTitle = (title) => {
  if (!title) return;
  const main = document.querySelector('main');
  const pageTitle = document.createElement('h4');
  pageTitle.innerText = title;
  main.appendChild(pageTitle);
};

const renderItemsTable = (tr, ligne) => {

  const tdNom = document.createElement('td');
  const nom = document.createElement('div');
  nom.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  nom.innerHTML = `${ligne.name}`;
  tdNom.appendChild(nom);
  tr.appendChild(tdNom);



  const tdType = document.createElement('td');
  const type = document.createElement('div');
  type.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  type.innerText = ligne.type;
  tdType.appendChild(type);
  tr.appendChild(tdType);

  const tdDescription = document.createElement('td');
  const description = document.createElement('div');
  description.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  description.innerText = ligne.description;
  tdDescription.appendChild(description);
  tr.appendChild(tdDescription);

  const tdMeetingDate = document.createElement('td');
  const meetingDate = document.createElement('div');
  meetingDate.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  meetingDate.innerText = `${addZeroInFrontIfUnder2Digits(ligne.meetingDate.date[2])}-${addZeroInFrontIfUnder2Digits(ligne.meetingDate.date[1])}-${ligne.meetingDate.date[0]}`;
  tdMeetingDate.appendChild(meetingDate);
  tr.appendChild(tdMeetingDate);

  const tdEtat = document.createElement('td');
  const etat = document.createElement('div');
  etat.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  etat.innerText = itemStateFrenchTranslation(ligne.state);
  tdEtat.appendChild(etat);
  tr.appendChild(tdEtat);

  const tdPrice = document.createElement('td');
  const price = document.createElement('div');
  price.className = 'd-flex align-items-center justify-content-center cellule-table-recherche';
  price.innerText = ligne.price === 0 ? "-" : `${ligne.price}€`;
  tdPrice.appendChild(price);
  tr.appendChild(tdPrice);

  const tdImage = document.createElement('td');
  const img = document.createElement('img');
  img.className = 'image-recherche';
  img.src = ligne.photo.accessPath;
  img.alt = ligne.name;
  img.className = 'img-thumbnail';
  img.style = 'max-height:125px';
  tdImage.appendChild(img);
  tr.appendChild(tdImage);
}

const renderLoading = () => {
  const main = document.querySelector("main");
  main.classList.add("no-scrollbar");
  main.innerHTML = `
  <div class="spinner-container h-100 d-flex justify-content-center align-items-center">
    <div class="spinner-border teclassNameimary" role="status">
        <span class="sr-only">Loading.classNamepan>
    </div>
  </div>
  `
}

export { clearPage, renderPageTitle, renderItemsTable, renderLoading };
