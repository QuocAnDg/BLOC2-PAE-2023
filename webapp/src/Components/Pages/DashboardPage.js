import Chart from 'chart.js/auto';

import {clearPage, renderItemsTable, renderLoading} from '../../utils/render';
import {
    getAllItemTypes,
    readAllItems,
} from "../../models/items";
import Navigate from "../Router/Navigate";
import { addAvailability } from "../../models/availabilities";
import {itemStateFrenchTranslation} from "../../utils/translator";

let chartAllItems;
let chartItemsAccepted;

const DashboardPage = async () => {
    clearPage();
    renderLoading()
    const allItems = await readAllItems();
    clearPage()
    await renderDashboard(allItems);
    await renderCharts(allItems);
};

// eslint-disable-next-line no-unused-vars
async function refreshCharts(){
    chartAllItems.destroy();
    chartItemsAccepted.destroy();
    const allItems = await readAllItems();
    await renderCharts(allItems);
}

async function renderDashboard(itemsList) {
    const main = document.querySelector('main');
    // const chartsWrapper = document.createElement('div')
    const containerWrapper = document.createElement('section');
    containerWrapper.className = 'p-3';

    const container = document.createElement('div');
    container.className = 'container bg-light mt-4 p-5 rounded-3';
    const container2 = document.createElement('div');
    container2.style = 'height: 100px';
    const itemsSearchWrapper = document.createElement('nav');
    itemsSearchWrapper.className = 'm-3 mb-0';

    const searchTitle = document.createElement('h2');
    searchTitle.style = "text-align: center";
    searchTitle.innerText = 'Recherche sur les objets';
    container.appendChild(searchTitle);

    const availabilityContainerWrapper = document.createElement('section');
    availabilityContainerWrapper.className = 'p-3';

    const availabilityContainer = document.createElement('div');
    availabilityContainer.className = 'container bg-light mt-4 p-3 rounded-3 mb-5';

    const availabilityTitle = document.createElement('h2');
    availabilityTitle.style = "text-align: center";
    availabilityTitle.innerText = 'Ajouter une disponibilité';
    availabilityContainer.appendChild(availabilityTitle);


    const availability = document.createElement('div');
    availability.innerHTML =
        `
      <div class="row justify-content-md-center ">
      <div class="col-3">
      <form>
      <div class="form-group mb-3">
      <label for="datepicker" class="mb-2">Sélectionnez une date:</label>
      
      <input type="date" id="datepicker" class="form-control">
      </div>
      <button type="submit" class="btn btn-primary" id="add-availability-date">Ajouter</button>
      <div id="errorMsg"></div>
      </form>
      </div>
      </div>
      `;

    const statisticsContainerWrapper = document.createElement('section');
    statisticsContainerWrapper.className = 'p-3';

    const statisticsContainer = document.createElement('div');
    statisticsContainer.className = 'container bg-light mt-4 p-3 rounded-3';

    const statisticsTitle = document.createElement('h2');
    statisticsTitle.style = "text-align: center";
    statisticsTitle.innerText = 'Statistiques';
    statisticsContainer.appendChild(statisticsTitle);


    const statistics = document.createElement('div');
    statistics.innerHTML =
        `
        
 

        <div class="row justify-content-md-center">
      <div class="col-2">
      <form>
      <div class="form-group">
      <label for="datepicker-start">Début de la période:</label>
      
      <input type="date" id="datepicker-start" class="form-control">
      </div>
      </form>
      </div>
      <div class="col-2">
      <form>
      <div class="form-group">
      <label for="datepicker-end">Fin de la période:</label>
      
      <input type="date" id="datepicker-end" class="form-control">
      </div>
      <div id="errorMsgPeriod"></div>
      </form>
      </div>
      </div>

      <div id="numberOfPropositions" style="text-align: center; margin: 20px; font-weight: bold;"></div>

        <div class="row justify-content-md-center">
      <div class="col-3">
      <canvas id="chartAllItems"></canvas>
      </div>
      <div class="col-3">
      <canvas id="chartItemsAccepted"></canvas>
      </div>
      </div>

      
        `;





    const searchTabs = document.createElement('div');
    searchTabs.className = 'nav nav-tabs nav-justified';
    searchTabs.id = 'myTab';
    searchTabs.role = 'tablist';
    searchTabs.innerHTML =
    `<button class="nav-link active" id="type-tab" data-bs-toggle="tab" data-bs-target="#type-tab-pane"
        type="button" role="tab" aria-controls="type-tab-pane" aria-selected="true">
        En fonction du montant du type ou de l'état de l'objet
    </button>
    <button class="nav-link" id="date-tab" data-bs-toggle="tab" data-bs-target="#date-tab-pane"
        type="button" role="tab" aria-controls="date-tab-pane" aria-selected="false">
        En fonction de la date à aller chercher
    </button>`;
    itemsSearchWrapper.appendChild(searchTabs);

    const tabsContent = document.createElement('div');
    tabsContent.className = 'tab-content border border-top-0 border-bot-0 p-3 bg-white';
    tabsContent.id = 'myTabContent';


    const typeTab = document.createElement('div');
    typeTab.className = 'tab-pane fade show active';
    typeTab.id = 'type-tab-pane';
    typeTab.role = 'tabpanel';
    typeTab.ariaLabel = 'type-tab';
    typeTab.tabIndex = '0';

    let itemDropdown = '';
    const allItemsTypes = await getAllItemTypes();
    allItemsTypes.forEach((itemType) => {
        itemDropdown += `<option value="${itemType}">${itemType}</option>`
    });

    let stateDropdown = '';
    const allItems = await readAllItems();
    const allStates = [];
    allItems.forEach((item) => {
        if(!allStates.includes(item.state)){
            allStates.push(item.state);
        }
    });
    allStates.forEach((state) => {
        stateDropdown += `<option value="${state}">${itemStateFrenchTranslation(state)}</option>`
    })

    const mostExpensivePrice = itemsList.reduce((prev, curr) => prev.price > curr.price ? prev : curr).price;

    typeTab.innerHTML = `
      <div class="row justify-content-md-center">
      <div class="col-2">
      <div class="form-group">
      <label for="minimum-price">Prix minimum (€):</label>
      <input type="number" id="minimum-price" value="0" min="0" step="0.01" class="form-control">
      </div>
      </div>
      
      <div class="col-2">
      <div class="form-group">
      <label for="maximum-price">Prix maximum (€)</label>
      <input type="number" id="maximum-price" value="${mostExpensivePrice}" min="0" step="0.01" class="form-control">
      </div>
      </div>
      
      <div class="col-2">
          <label for="itemType">Type de l'objet:</label>
          <select name="itemType" id="itemType" class="form-select">
          <option selected></option>
            ${itemDropdown};
          </select> 
      </div>
      
      <div class="col-2">
          <label for="itemState">Etat de l'objet:</label>
          <select name="itemState" id="itemState" class="form-select">
          <option selected></option>
            ${stateDropdown};
          </select> 
      </div>
      
      <div class="col-2 align-self-end">
        <button type="button" class="btn btn-primary" id="btn-search-price-type">Rechercher</button>
      </div>
      
      </div>
    `

    const dateTab = document.createElement('div');
    dateTab.className = 'tab-pane fade';
    dateTab.id = 'date-tab-pane';
    dateTab.role = 'tabpanel';
    dateTab.ariaLabel = 'date-tab';
    dateTab.tabIndex = '0';

    dateTab.innerHTML = `
     
       <div class="row justify-content-md-center">
      <div class="col-2">
      <div class="form-group">
      <label for="search-date-start">Début de la période:</label>
      
      <input type="date" id="search-date-start" class="form-control">
      </div>
      </div>
      <div class="col-2">

      <div class="form-group">
      <label for="search-date-end">Fin de la période:</label>
      <input type="date" id="search-date-end" class="form-control">
      </div>
      </div>
      
    
      <div class="col-2 align-self-end">
        <button type="button" class="btn btn-primary" id="btn-search-date">Rechercher</button>
      </div>
 
      </div>
  
    
    `

    tabsContent.appendChild(typeTab);
    tabsContent.appendChild(dateTab);

    const tableWrapper = document.createElement('div');
    tableWrapper.className = 'container overflow-auto border mt-3 p-3 pb-0 bg-light text-center';
    tableWrapper.id = 'table-recherche-wrapper';

    const table = document.createElement('table');
    table.className = 'table table-bordered table-hover bg-white';

    const thead = document.createElement('thead');
    thead.innerHTML = `
    <tr>
        <th class="col">Nom</th>
        <th class="col">Type</th>
        <th class="col-3">Description</th>
        <th class="col">Date de réception</th>
        <th class="col">État</th>
        <th class="col">Prix</th>
        <th class="col">Photo</th>
    </tr>`;

    let tbody = document.createElement('tbody');
    tbody.id = "item-table-body";
    itemsList.forEach((ligne) => {
        const tr = document.createElement('tr');
        tr.classList.add("cursor-pointer");
        renderItemsTable(tr, ligne);

        tr.addEventListener('click', (e) => {
            e.preventDefault();
            Navigate('/item?id=', ligne.id);
        })

        tbody.appendChild(tr);
    });

    const itemDetailsWrapper = document.createElement('div');
    itemDetailsWrapper.id = 'item-details-wrapper';

    table.appendChild(thead);
    table.appendChild(tbody);
    tableWrapper.appendChild(table);
    tabsContent.appendChild(tableWrapper);
    tabsContent.appendChild(itemDetailsWrapper);
    itemsSearchWrapper.appendChild(tabsContent);

    statisticsContainerWrapper.appendChild(statisticsContainer);
    statisticsContainer.appendChild(statistics);
    main.appendChild(statisticsContainerWrapper);

    container.appendChild(itemsSearchWrapper);
    containerWrapper.appendChild(container);
    main.appendChild(containerWrapper);

    availabilityContainerWrapper.appendChild(availabilityContainer);
    availabilityContainerWrapper.appendChild(container2);
    availabilityContainer.appendChild(availability);
    main.appendChild(availabilityContainerWrapper);



    document.getElementById("datepicker").setAttribute("min", new Date().toISOString().slice(0, 10));


    document.getElementById("add-availability-date").addEventListener('click', async (e) =>{
        e.preventDefault();
        const selectedDate = new Date(document.getElementById("datepicker").value)
        if (selectedDate < Date.now()) {
            document.querySelector('#errorMsg').innerHTML = "Vous ne pouvez pas sélectionner une date passée";
        }
        else if (selectedDate.getDay() !== 6){
            document.querySelector('#errorMsg').innerHTML = "Seuls les samedis peuvent être sélectionnés"
        }
        else{
            await addAvailability(selectedDate.toISOString().split('T')[0]);
            clearPage();
            Navigate('dashboard');
        }

    });

    document.getElementById("btn-search-price-type").addEventListener('click', async(e) => {
       e.preventDefault();

        tbody = document.getElementById("item-table-body");
        tbody.innerHTML = '';
        const montantMin = document.getElementById("minimum-price").value;
        const montantMax = document.getElementById("maximum-price").value;
        const itemType =  document.getElementById("itemType").value;
        const itemState = document.getElementById("itemState").value;

        itemsList.forEach((ligne) => {
            if((montantMin === 0 || ligne.price >= montantMin) && (montantMax === 0 || ligne.price <= montantMax)
                && (itemType === "" || ligne.type === itemType) && (itemState === "" || ligne.state === itemState)){
                const tr = document.createElement('tr');

                renderItemsTable(tr, ligne);
                tr.addEventListener('click', () => {
                    e.preventDefault();
                    Navigate('/item?id=', ligne.id);
                })
                tr.classList.add("cursor-pointer")
                tbody.appendChild(tr);
            }

        })
    });

    document.getElementById("btn-search-date").addEventListener('click', async(e) => {
        e.preventDefault();

        tbody = document.getElementById("item-table-body");
        tbody.innerHTML = '';
        const startDate = new Date(document.getElementById("search-date-start").value);
        const endDate = new Date(document.getElementById("search-date-end").value);

        itemsList.forEach((ligne) => {
            if(ligne.storeDepositDate != null){
                const storeDepositDate =  new Date(ligne.storeDepositDate[0], ligne.storeDepositDate[1] - 1,ligne.storeDepositDate[2])
                if(storeDepositDate >= startDate && storeDepositDate <= endDate){
                    const tr = document.createElement('tr');

                    renderItemsTable(tr, ligne);
                    tr.addEventListener('click', () => {
                        e.preventDefault();
                        Navigate('/item?id=', ligne.id);
                    })
                    tr.classList.add("cursor-pointer")
                    tbody.appendChild(tr);
                }
            }


        })
    });

    const currentDate = new Date();
    const startDate = new Date();
    startDate.setMonth(currentDate.getMonth() - 12);  
    document.getElementById('datepicker-start').defaultValue = startDate.toISOString().slice(0, 10);
    document.getElementById('datepicker-end').defaultValue = currentDate.toISOString().slice(0, 10);

    const startDateInput = document.getElementById("datepicker-start");
    startDateInput.addEventListener("change", refreshCharts);
    const endDateInput = document.getElementById("datepicker-end");
    endDateInput.addEventListener("change", refreshCharts);
    



    
}


async function renderCharts(itemsList){
    const startDate = new Date(document.getElementById("datepicker-start").value);
    const endDate = new Date(document.getElementById("datepicker-end").value);

    let itemsAccepted = 0;
    let itemsDenied = 0;
    let itemsProposed = 0;
    let itemsSold = 0;
    let itemsNotSold = 0;
    for (let index = 0; index < itemsList.length; index+=1) {
       const meetingDate = new Date(itemsList[index].meetingDate.date[0], itemsList[index].meetingDate.date[1]-1, itemsList[index].meetingDate.date[2]);
        if(meetingDate >= startDate && meetingDate <= endDate){
            if(itemsList[index].state === 'denied'){
                itemsDenied += 1;
            }
            else if (itemsList[index].state === 'proposed'){
                itemsProposed += 1;
            }
            else{
                itemsAccepted += 1;
                if(itemsList[index].state === 'sold'){
                    itemsSold += 1;
                }
                else{
                    itemsNotSold += 1;
                }
            }
        }
    }

    document.getElementById("numberOfPropositions").innerText = `Nombre de propositions d'objets sur cette période : ${itemsDenied + itemsProposed + itemsAccepted}`;


  


  // eslint-disable-next-line no-new, no-unused-vars
    chartAllItems = new Chart(document.getElementById('chartAllItems'), {
    type: 'pie',
    data: {
      labels: ['Acceptés', 'Refusés', 'En attente'],
      datasets: [{
        label: 'Nombre d\'objets',
        data: [itemsAccepted, itemsDenied, itemsProposed],
          backgroundColor: [
              'rgb(0, 102, 0)',
              'rgb(204, 0, 0)',
              'rgb(0, 0, 150)'
          ],
        borderWidth: 1
      }]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Propositions d\'objets'
            }
        }
    }
  });

  // eslint-disable-next-line no-new, no-unused-vars
  chartItemsAccepted = new Chart(document.getElementById('chartItemsAccepted'), {
    type: 'pie',
    data: {
      labels: ['Vendus', 'Non vendus'],
      datasets: [{
        label: 'Nombre d\'objets',
        data: [itemsSold, itemsNotSold],
        borderWidth: 1
      }]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Propositions acceptées'
            }
        }
    }
  });
};

export default DashboardPage;