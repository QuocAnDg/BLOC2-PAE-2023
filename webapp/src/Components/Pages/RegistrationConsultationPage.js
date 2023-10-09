import { readAllUsers, updateRoleUser } from '../../models/users';
import { getAuthenticatedUser } from '../../utils/auths';
import {clearPage, renderLoading} from '../../utils/render';
import { userRoleFrenchTranslation } from '../../utils/translator';
import Navigate from '../Router/Navigate';
import {
  addZeroInFrontIfUnder2Digits
} from "../../utils/utils";
import ProfilePage from './ProfilePage';

const RegistationConsultationPage = async () => {

  const user = await getAuthenticatedUser();

  if (user===undefined) {
    Navigate('/login');

  } else 
  if (user.role!=="admin"){
    Navigate('/');

  } else {
    clearPage();
    renderLoading();
    const registrations = await readAllUsers();
    clearPage();
    renderRegistrationConsultationPage(registrations);
  }
};

function renderRegistrationConsultationPage(registrations) {
  const main = document.querySelector('main');
  main.classList.add("no-scrollbar");
  const banner = document.createElement('section');
  banner.className = 'p-3';

  const divRegConsultPage = document.createElement('div');
  divRegConsultPage.className = 'container d-flex flex-column mt-5';

  const divTableTitle = document.createElement('div');
  const tableTitle = document.createElement('h2');
  tableTitle.innerText = 'Information sur les inscriptions';

  const divTableAndButton = document.createElement('div');
  divTableAndButton.className = 'bg-white border rounded p-3';

  const divItemTable = document.createElement('div');
  divItemTable.className = 'table-responsive';

  const divTable = document.createElement('table');
  divTable.className = 'table table-bordered text-center';
  const divThead = document.createElement('thead');
  const divTrHead = document.createElement('tr');
  const divThHead1 = document.createElement('th');
  const divThHead2 = document.createElement('th');
  divThHead2.innerText = "Date d'inscription";
  const divThHead3 = document.createElement('th');
  divThHead3.innerText = 'Nom et prénom';
  const divThHead4 = document.createElement('th');
  divThHead4.innerText = 'Email';
  const divThHead5 = document.createElement('th');
  divThHead5.innerText = 'Numéro de GSM';
  const divThHead6 = document.createElement('th');
  divThHead6.innerText = 'Photo/Avatar';
  const divThHead7 = document.createElement('th');
  divThHead7.innerText = 'Rôle';

  divTrHead.appendChild(divThHead1);
  divTrHead.appendChild(divThHead2);
  divTrHead.appendChild(divThHead3);
  divTrHead.appendChild(divThHead4);
  divTrHead.appendChild(divThHead5);
  divTrHead.appendChild(divThHead6);
  divTrHead.appendChild(divThHead7);
  divThead.appendChild(divTrHead);
  divTable.appendChild(divThead);

  const divTbody = document.createElement('tbody');

  registrations?.forEach((registration) => {
    const divTrBody = document.createElement('tr');
    const divTdBody1 = document.createElement('td');
    divTdBody1.className = 'text-center align-middle';

    const divCheckBoxGroup = document.createElement('div');
    divCheckBoxGroup.className = 'container p-1';
    const divCheckBox = document.createElement('div');
    divCheckBox.className = 'input-group-text d-flex justify-content-center';
    const checkbox = document.createElement('input');
    checkbox.className = 'form-check-input mt-0';
    checkbox.type = 'checkbox';
    checkbox.value = registration.id;
    if (registration.role==='admin'){
      checkbox.disabled = true;
    }

    const divTdBody2 = document.createElement('td');
    const dateRegistration = "".concat(addZeroInFrontIfUnder2Digits(registration.registerDate[2]), "-",addZeroInFrontIfUnder2Digits(registration.registerDate[1]), "-",addZeroInFrontIfUnder2Digits(registration.registerDate[0]));
    divTdBody2.innerText = dateRegistration;
    divTdBody2.className = "align-middle";
    const divTdBody3 = document.createElement('td');
    divTdBody3.innerText = registration.firstname.concat(' ').concat(registration.lastname);
    divTdBody3.className = "align-middle";
    const divTdBody4 = document.createElement('td');
    divTdBody4.innerText = registration.email;
    divTdBody4.className = "align-middle";
    const divTdBody5 = document.createElement('td');
    divTdBody5.innerText = registration.phoneNumber;
    divTdBody5.className = "align-middle";
    const divTdBody6 = document.createElement('td');
    divTdBody6.className = 'cursor-pointer';
    const imgUser = document.createElement('img');
    imgUser.src = registration.profilePhoto.accessPath;
    imgUser.width = 90;
    imgUser.height = 90;

    const divTdBody7 = document.createElement('td');
    const userRole = userRoleFrenchTranslation(registration.role);
    divTdBody7.innerText = userRole;
    divTdBody7.className = "align-middle";

    divCheckBox.appendChild(checkbox);
    divCheckBoxGroup.appendChild(divCheckBox);
    divTdBody1.appendChild(divCheckBoxGroup);
    divTrBody.appendChild(divTdBody1);
    divTrBody.appendChild(divTdBody2);
    divTrBody.appendChild(divTdBody3);
    divTrBody.appendChild(divTdBody4);
    divTrBody.appendChild(divTdBody5);
    divTdBody6.appendChild(imgUser);
    divTrBody.appendChild(divTdBody6);
    divTrBody.appendChild(divTdBody7);
    divTbody.appendChild(divTrBody);
    divTable.appendChild(divTbody);

    divTdBody6.addEventListener('click', () => {
      ProfilePage(registration);
    });
  });

  const divConfirmButton = document.createElement('div');
  divConfirmButton.className = 'text-end mt-5';

  const confirmButton = document.createElement('input');
  confirmButton.value = 'Donner le status "aidant" aux personnes sélectionnées';
  confirmButton.type = 'submit';
  confirmButton.className = 'btn text-white';
  confirmButton.id = 'fontColor';

  const confirmAdminButton = document.createElement('input');
  confirmAdminButton.value = 'Donner le status "responsable" aux personnes sélectionnées';
  confirmAdminButton.style.marginRight = "10px";
  confirmAdminButton.type = 'submit';
  confirmAdminButton.className = 'btn text-white';
  confirmAdminButton.id = 'fontColor';

  divConfirmButton.appendChild(confirmAdminButton);
  divConfirmButton.appendChild(confirmButton);
  divTableTitle.appendChild(tableTitle);
  divItemTable.appendChild(divTable);
  divTableAndButton.appendChild(divItemTable);
  divTableAndButton.appendChild(divConfirmButton);
  divRegConsultPage.appendChild(divTableTitle);
  divRegConsultPage.appendChild(divTableAndButton);
  banner.appendChild(divRegConsultPage);
  main.appendChild(banner);

  confirmButton.addEventListener('click', updateRole);
  confirmAdminButton.addEventListener('click', updateRoleAdmin);
}

function updateRole(){
  const checkboxes = document.querySelectorAll('input:checked');

  checkboxes.forEach(async (checkbox) => {
    await updateRoleUser(checkbox.value, "helper");
  });
  window.location.reload();
}

function updateRoleAdmin(){
  const checkboxes = document.querySelectorAll('input:checked');

  checkboxes.forEach(async (checkbox) => {
    await updateRoleUser(checkbox.value, "admin");
  });
  window.location.reload();
}

export default RegistationConsultationPage;
