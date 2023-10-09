import HomePage from '../Pages/HomePage';
import Logout from '../Logout/Logout';
import LoginPage from '../Pages/LoginPage';
import RegisterPage from '../Pages/RegisterPage';
import RegistationConsultationPage from '../Pages/RegistrationConsultationPage';
import OfferItemsConsultationPage from '../Pages/OfferItemsConsultationPage';
import DashboardPage from '../Pages/DashboardPage';
// import ItemsListPage from "../Pages/ItemsListPage";
import ItemPage from "../Pages/ItemPage";
import ProfilePage from '../Pages/ProfilePage'
import OfferItemPage from '../Pages/OfferItemPage'

const routes = {
  '/': HomePage,
  '/login': LoginPage,
  '/register': RegisterPage,
  '/logout': Logout,
  '/consultRegistrations': RegistationConsultationPage,
  '/consultOfferItems': OfferItemsConsultationPage,
  '/dashboard': DashboardPage,
  // '/itemslist': ItemsListPage,
  '/item': ItemPage,
  '/profile': ProfilePage,
  '/offer': OfferItemPage,
};

export default routes;
