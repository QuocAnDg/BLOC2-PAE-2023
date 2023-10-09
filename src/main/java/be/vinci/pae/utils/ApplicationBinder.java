package be.vinci.pae.utils;

import be.vinci.pae.domain.AvailabilityUCC;
import be.vinci.pae.domain.AvailabilityUCCImpl;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.domain.ItemUCC;
import be.vinci.pae.domain.ItemUCCImpl;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.domain.NotificationUCCImpl;
import be.vinci.pae.domain.PhotoUCC;
import be.vinci.pae.domain.PhotoUCCImpl;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.domain.UserUCCImpl;
import be.vinci.pae.services.AvailabilityDAO;
import be.vinci.pae.services.AvailabilityDAOImpl;
import be.vinci.pae.services.ItemDAO;
import be.vinci.pae.services.ItemDAOImpl;
import be.vinci.pae.services.NotificationDAO;
import be.vinci.pae.services.NotificationDAOImpl;
import be.vinci.pae.services.PhotoDAO;
import be.vinci.pae.services.PhotoDAOImpl;
import be.vinci.pae.services.UserDAO;
import be.vinci.pae.services.UserDAOImpl;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.services.utils.DALServicesImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * ApplicationBinder class.
 */
@Provider
public class ApplicationBinder extends AbstractBinder {

  /**
   * Bind an implementation with its service.
   */
  @Override
  protected void configure() {
    bind(DALServicesImpl.class).to(DALBackendServices.class).to(DALServices.class)
        .in(Singleton.class);
    bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
    bind(UserDAOImpl.class).to(UserDAO.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(ItemDAOImpl.class).to(ItemDAO.class).in(Singleton.class);
    bind(ItemUCCImpl.class).to(ItemUCC.class).in(Singleton.class);
    bind(NotificationDAOImpl.class).to(NotificationDAO.class).in(Singleton.class);
    bind(NotificationUCCImpl.class).to(NotificationUCC.class).in(Singleton.class);
    bind(PhotoDAOImpl.class).to(PhotoDAO.class).in(Singleton.class);
    bind(PhotoUCCImpl.class).to(PhotoUCC.class).in(Singleton.class);
    bind(AvailabilityDAOImpl.class).to(AvailabilityDAO.class).in(Singleton.class);
    bind(AvailabilityUCCImpl.class).to(AvailabilityUCC.class).in(Singleton.class);
  }
}
