// eslint-disable-next-line no-unused-vars
import { Navbar as BootstrapNavbar } from 'bootstrap';
import { getAuthenticatedUser, isAuthenticated } from '../../utils/auths';
import { updateNotifications, markAllNotificationsAsRead } from '../../models/notifications';

const Navbar = async () => {
  renderNavbar();
  const authenticatedUser = await getAuthenticatedUser();
  if (authenticatedUser !== undefined) {
    await updateNotifications(authenticatedUser.id);
    setInterval(async () => {
      await updateNotifications(authenticatedUser.id);
    }, 10000);
  }
};

async function renderNavbar() {
  
  const authenticatedUser = await getAuthenticatedUser(); // take into account remember me check (session or local storage)

  const anonymousUserNavbar = `
  <nav class="navbar navbar-expand-lg" id="fontColor">
    <div class="container-fluid">
      <a href="#"><img class="nav-link" aria-current="page" data-uri="/" src = 'logo.png' alt = "logo" width = "150"></a>
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
        <ul class="navbar-nav">
          <li class="nav-item me-4">
            <button class="nav-link btn btn-warning fw-bold text-dark" href="#" data-uri="/offer">Proposer un objet</button>
          </li>
          <li id="loginItem" class="nav-item">
            <a class="nav-link" href="#" data-uri="/login">Connexion</a>
          </li>
          <li id="registerItem" class="nav-item">
            <a class="nav-link" href="#" data-uri="/register">Inscription</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  `;

  let authenticatedUserNavbar = "";

  if (authenticatedUser!==undefined){
    authenticatedUserNavbar = `
    <nav class="navbar navbar-expand-lg" id="fontColor">
      <div class="container-fluid navbar-container">
        
      <a href="#"><img class="nav-link" aria-current="page" data-uri="/" src = 'logo.png' alt = "logo" width = "150 "></a>
        <button
          class="navbar-toggler navbar-dark me-3"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
          <ul class="navbar-nav p-2">
            <li class="nav-item me-4">
            <a class="nav-link btn btn-warning fw-bold text-dark" href="#" data-uri="/offer">Proposer un objet</a>
            </li>
          </ul>
          <div class="nav-item dropdown notifications-dropdown me-4 ms-2">
              <a class="nav-link" id="notificationButton" href="#" role="button" data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
                  <i class="fa fa-solid fa-bell fa-2x"></i>
                  <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none" id="notificationPill">
                  </span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end me-2" id="notificationsDropdown" aria-labelledby="notificationsDropdown">
                  <li class="text-center fw-bold fs-5">Notifications</li>
                  <li><hr class="dropdown-divider"></li>
                  <ul class="list-group list-group-flush" id="notificationsList">
                  </ul>
              </ul>
          </div>
          <div class="nav-item dropdown profile-button me-3">
            <a class="nav-link dropdown-toggle text-white dp-profile-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i><img src="${authenticatedUser.profilePhoto.accessPath}" class="rounded-circle" width="60" height="60"></i>
            </a>
            <ul class="dropdown-menu dropdown-menu-end me-2" aria-labelledby="navbarDropdown">
             <li class="text-center fw-bold fs-5">${authenticatedUser.firstname} ${authenticatedUser.lastname}</li>
             <li><hr class="dropdown-divider"></li>
             <li><a class="dropdown-item" href="#" data-uri="/profile">Votre profil</a></li>
  <!--           <li><a class="dropdown-item adminAndHelper" href="#" data-uri="/itemslist">Liste des objets</a></li>-->
             <li><a class="dropdown-item adminPage" href="#" data-uri="/consultOfferItems">Consulter les propositions d'objets</a></li>
             <li><a class="dropdown-item adminPage" href="#" data-uri="/consultRegistrations">Consulter les inscriptions</a></li>
             <li><a class="dropdown-item adminAndHelper" href="#" data-uri="/dashboard">Accéder au tableau de bord</a></li>
             <li><hr class="dropdown-divider"></li>
             <li><a class="dropdown-item" href="#" data-uri="/logout">Se déconnecter</a></li>
            </ul>
          </div>
        </div>
      </div>
    </nav>
    `;
  }

  const navbar = document.querySelector('#navbarWrapper');

  navbar.innerHTML = isAuthenticated() ? authenticatedUserNavbar : anonymousUserNavbar;

  if (isAuthenticated()){
    document.getElementById("notificationButton").addEventListener("click", async () => {
      await markAllNotificationsAsRead(authenticatedUser.id);
      document.querySelector("#notificationPill").classList.add("d-none");
    });
  }

  if (authenticatedUser?.role !== "admin") {
  const adminPages = document.querySelectorAll('.adminPage');
  adminPages?.forEach((adminPage) =>{
    adminPage.classList.add('d-none');
  })
  }
  if (authenticatedUser?.role !== "admin" && authenticatedUser?.role !== "helper"){
    const adminAndHelperPages = document.querySelectorAll('.adminAndHelper');
    adminAndHelperPages?.forEach((adminAndHelperPage) =>{
      adminAndHelperPage.classList.add('d-none');
    })
  }
  
}

export default Navbar;
