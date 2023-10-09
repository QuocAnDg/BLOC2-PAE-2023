import {
  getAllItemTypes,
  readHomePageItems,
  searchByName, searchByState, searchByType
} from "../../models/items";
import {itemStateFrenchTranslation} from "../../utils/translator";
import {clearPage} from "../../utils/render";
import {replaceDotWithComma} from "../../utils/utils";

const HomePage = async () => {
  clearPage();
  const main = document.querySelector('main');
  main.classList.add("no-scrollbar");
  main.innerHTML = `
  <section>
    <div class="ressourcerie-info p-3 d-flex justify-content-around">
      <p class="text-center text-white mb-0 fw-bold">Rue de Heuseux 77ter,
         4671 Blégny
      </p>

      
    </div>
    <div class="banner-div d-flex flex-column justify-content-center align-items-center">
      <h1 class="banner-text text-white"><span class="ressourcerie-name">RessourceRie</span>, LA solution optimale</h1> 
      <h1 class="banner-text text-white">pour vous aider à vous débarrasser des objets que vous ne voulez plus</h1>
      <h5 class="banner-text2 mt-3">Faites appel à nos services et nous irons chercher vos objets au parc à conteneurs.</h5>

    </div>
  </section>
  <section class="py-5 px-5 d-flex w-100 justify-content-between">
    <div class="btn-group">
      <button id="fontColor" class="btn btn-secondary dropdown-toggle item-type-btn me-3" type="button" data-bs-toggle="dropdown" data-bs-auto-close="true" aria-expanded="false">
        Types d'objet
      </button>
      <ul class="dropdown-menu item-type-menu">
        <li><a class="dropdown-item all-types">Tous les types</a></li>
      </ul>
      <button id="fontColor" class="btn btn-secondary dropdown-toggle item-type-btn" type="button" data-bs-toggle="dropdown" data-bs-auto-close="true" aria-expanded="false">
        Etats
      </button>
      <ul class="dropdown-menu item-type-menu">
        <li><a class="dropdown-item in_store_btn cursor-pointer">En magasin</a></li>
        <li><a class="dropdown-item for_sale_btn cursor-pointer">En vente</a></li>
        <li><a class="dropdown-item sold_btn cursor-pointer">Vendu</a></li>
      </ul>
    </div>
    
    <div class="homepage-search">
      
      <input type="text" class="search-input" placeholder="Rechercher par nom">
      <div class="suggestions-box">
      </div>
      <i class="fa fa-search"></i>
    </div>
    
    <div></div>
    
  </section>
  <section id="carousel-section">
    
    <i id="arrow-left" class="bi bi-caret-left-fill"></i>
    <div class="carousel-container">
      
      <div class="carousel d-flex">
           
      </div>
      
    </div>
    <i id="arrow-right" class="bi bi-caret-right-fill"></i>
  </section>
  `;

  const carousel = document.querySelector('.carousel');
  carousel.innerHTML = `
    <div class="spinner-container h-100 d-flex justify-content-center align-items-center">
      <div class="spinner-border teclassNameimary" role="status">
          <span class="sr-only">Loading.classNamepan>
      </div>
    </div>
  `
  const items = await readHomePageItems();
  const types = await getAllItemTypes();

  carousel.innerHTML = "";

  renderItemTypes(types, items);

  addSuggestionOnSearches(items);

  addSearchEventListener(items);

  renderItems(items);

  addScrollingToCarousel();

  addArrowEventListeners();

  addStateButtonEventListener();

};

function addStateButtonEventListener() {
  const inStoreBtn = document.querySelector('.in_store_btn');
  const forSaleBtn = document.querySelector('.for_sale_btn');
  const soldBtn = document.querySelector('.sold_btn');
  const carousel = document.querySelector('.carousel');

  inStoreBtn.addEventListener('click', async () => {
    const itemsFiltered = await searchByState("in_store");
    carousel.innerHTML = ``;
    renderItems(itemsFiltered);
  })

  forSaleBtn.addEventListener('click', async () => {
    const itemsFiltered = await searchByState("for_sale");
    carousel.innerHTML = ``;
    renderItems(itemsFiltered);
  })

  soldBtn.addEventListener('click', async () => {
    const itemsFiltered = await searchByState("sold");
    carousel.innerHTML = ``;
    renderItems(itemsFiltered);
  })
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


function addSearchEventListener(items) {
  const searchInput = document.querySelector('.search-input');
  const searchIcon = document.querySelector('.homepage-search i');
  searchInput.addEventListener('keyup', (e) => {
    if (e.key === 'Enter') {
      readInputAndRenderItems(items, searchInput);
    }
  })
  searchIcon.addEventListener('click', () => {
    readInputAndRenderItems(items, searchInput);
  })
}

async function readInputAndRenderItems(items, searchInput) {
  const carousel = document.querySelector('.carousel');
  const input = searchInput.value;
  const itemsFiltered = await searchByName(input);
  console.log(itemsFiltered);
  carousel.innerHTML = ``;
  renderItems(itemsFiltered);
}

function renderItems(items) {
  const carousel = document.querySelector('.carousel');
  const arrowLeft = document.querySelector('#arrow-left');
  const arrowRight = document.querySelector('#arrow-right');

  if(items.length === 0){
    document.querySelector('.carousel').innerHTML =  "<b>Aucun objet ne correspond à vos critères</b>";
    arrowLeft.classList.add('d-none');
    arrowRight.classList.add('d-none');
    return;
  }

  items.forEach((item) => {
    arrowLeft.classList.remove('d-none');
    arrowRight.classList.remove('d-none');
    const itemContainer = document.createElement('div');
    itemContainer.classList.add('item-container');

    const ribbonDiv = document.createElement('div');
    ribbonDiv.classList.add('ribbon');
    const ribbonWord = document.createElement('span');
    ribbonWord.innerHTML = `${itemStateFrenchTranslation(item.state)}`;
    if (item.state === 'for_sale') {
      ribbonWord.classList.add('ribbon-blue');
    } else if (item.state === 'sold') {
      ribbonWord.classList.add('ribbon-red');
    } else {
      ribbonWord.classList.add('ribbon-black');
    }
    itemContainer.appendChild(ribbonDiv);
    ribbonDiv.appendChild(ribbonWord);

    const imageDiv = document.createElement('div');
    const image = document.createElement('img');
    imageDiv.classList.add('image-block');
    image.src=item.photo.accessPath;
    carousel.appendChild(itemContainer);
    itemContainer.appendChild(imageDiv);
    imageDiv.appendChild(image);

    const bottomDiv = document.createElement('div');
    bottomDiv.classList.add('container', 'item-container-bottom');
    const bottomRow = document.createElement('div');
    bottomRow.classList.add('row');
    const itemTypeDiv = document.createElement('div');
    itemTypeDiv.classList.add('col-md-4', 'ps-3', 'me-auto', 'ms-2');
    itemTypeDiv.innerHTML = `${item.type}`;

    const itemPriceDiv = document.createElement('div');

    if (item.state !== 'in_store') {
      itemPriceDiv.classList.add('col-md-4', 'offset-md-2', 'text-end');
      itemPriceDiv.innerHTML = `${replaceDotWithComma(item.price)}€`;
    }

    bottomDiv.appendChild(bottomRow);
    bottomRow.appendChild(itemTypeDiv);
    if (item.state !== 'in_store') {
      bottomRow.appendChild(itemPriceDiv);
    }

    itemContainer.appendChild(bottomDiv);
  })


}

function renderItemTypes(types, items) {
  const menu = document.querySelector('.item-type-menu');
  const carousel = document.querySelector('.carousel');
  types.forEach((type) => {
    const line = document.createElement('li');
    const lineContent = document.createElement('a');
    lineContent.classList.add('dropdown-item', 'cursor-pointer');
    lineContent.innerHTML = `${type}`;
    menu.appendChild(line);
    line.appendChild(lineContent);

    line.addEventListener('click', async (e) => {
      e.preventDefault();

      carousel.innerHTML = '';
      const itemsFiltered = await searchByType(type);
      console.log(itemsFiltered);
      renderItems(itemsFiltered);
    })
  })

  const allTypesButton = document.querySelector('.all-types');
  allTypesButton.addEventListener('click', (e) => {
    e.preventDefault();
    carousel.innerHTML = '';
    renderItems(items);
  })
}

function addScrollingToCarousel() {
  const carousel = document.querySelector('.carousel');
  // const firstImage = document.querySelectorAll('.img')[0];
  const arrowIcons = document.querySelectorAll('i');

  arrowIcons.forEach((icon) => {
    icon.addEventListener('click', () => {

    })
  })

  let isDragStart = false;
  let previousPositionX;
  let previousScrollLeft

  carousel.addEventListener('mousemove', (e) => {
    if (!isDragStart) return;
    e.preventDefault();
    carousel.classList.add("dragging");
    const positionXDifference = e.pageX - previousPositionX;
    carousel.scrollLeft = previousScrollLeft - positionXDifference;
  });
  carousel.addEventListener('mousedown', (e) => {
    isDragStart = true;
    previousPositionX = e.pageX;
    previousScrollLeft = carousel.scrollLeft;
  });
  carousel.addEventListener('mouseup', () => {
    isDragStart = false;
    carousel.classList.remove("dragging");
  })
}

function addArrowEventListeners() {
  const carousel = document.querySelector('.carousel');
  const arrowIcons = document.querySelectorAll('#carousel-section i');

  const firstItem = document.querySelectorAll('.item-container')[0];

  const scrollDistance = firstItem.clientWidth + 50;

  arrowIcons.forEach((arrowIcon) => {
    arrowIcon.addEventListener('click', () => {
      carousel.scrollLeft += arrowIcon.id === "arrow-left" ? -scrollDistance : scrollDistance;
    });
  });
}

export default HomePage;
