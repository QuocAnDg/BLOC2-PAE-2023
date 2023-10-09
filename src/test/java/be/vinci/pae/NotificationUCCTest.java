package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.domain.NotificationUCCImpl;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.services.NotificationDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.ApplicationBinder;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * The type Notification ucc test.
 */
public class NotificationUCCTest {
  @Mock
  private NotificationDAO notificationDAOmock;

  @Mock
  private DALServices dalServicesMock;

  @Mock
  private UserUCC userUCCMock;

  private NotificationUCC notificationUCC;
  private DomainFactory domainFactory;

  /**
   * Sets up.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    AbstractBinder testApplicationBinder = new ApplicationBinder() {
      @Override
      protected void configure() {
        bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
        bind(notificationDAOmock).to(NotificationDAO.class);
        bind(dalServicesMock).to(DALServices.class);
        bind(userUCCMock).to(UserUCC.class);
        bind(NotificationUCCImpl.class).to(NotificationUCC.class).in(Singleton.class);
      }
    };
    ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
    ServiceLocatorUtilities.bind(locator, testApplicationBinder);
    notificationUCC = locator.getService(NotificationUCC.class);
    domainFactory = locator.getService(DomainFactory.class);
  }

  @Test
  public void testViewAllNotificationsOfUser() {
    List<NotificationDTO> expectedNotifications = new ArrayList<>();
    expectedNotifications.add(domainFactory.getNotificationDTO());

    Mockito.when(notificationDAOmock.getNotificationsOfUser(anyInt()))
        .thenReturn(expectedNotifications);

    List<NotificationDTO> actualNotifications = notificationUCC.viewAllNotificationsOfUser(1);
    assertEquals(expectedNotifications, actualNotifications);
    Mockito.verify(dalServicesMock).startTransaction();
    Mockito.verify(notificationDAOmock).getNotificationsOfUser(1);
    Mockito.verify(dalServicesMock).commit();
  }

  @Test
  public void testMarkAsRead() {
    int userID = 123;

    assertDoesNotThrow(() ->
        notificationUCC.markAsRead(userID)
    );

    Mockito.verify(dalServicesMock).startTransaction();
    Mockito.verify(notificationDAOmock).markAsRead(userID);
    Mockito.verify(dalServicesMock).commit();
  }

  @Test
  public void testCreateProposalNotification() {
    List<UserDTO> helpersList = new ArrayList<>();
    UserDTO helper1 = domainFactory.getUserDTO();
    helper1.setId(1);
    UserDTO helper2 = domainFactory.getUserDTO();
    helper2.setId(2);
    helpersList.add(helper1);
    helpersList.add(helper2);

    NotificationDTO notificationDTO = domainFactory.getNotificationDTO();
    notificationDTO.setId(1);

    Mockito.when(userUCCMock.findAllHelpersAndAdmin()).thenReturn(helpersList);
    Mockito.when(notificationDAOmock.createNotification(anyInt(), Mockito.eq("proposal")))
        .thenReturn(notificationDTO);

    assertDoesNotThrow(() ->
        notificationUCC.createProposalNotification(1)
    );

    Mockito.verify(notificationDAOmock)
        .createIndividualNotification(Mockito.eq(1), Mockito.eq(1));
    Mockito.verify(notificationDAOmock)
        .createIndividualNotification(Mockito.eq(1), Mockito.eq(2));
    Mockito.verify(dalServicesMock).startTransaction();
    Mockito.verify(dalServicesMock).commit();
  }

  @Test
  public void testCreateDecisionNotification() {
    int itemID = 123;
    int userID = 456;
    NotificationDTO createdNotification = domainFactory.getNotificationDTO();
    Mockito.when(notificationDAOmock.createNotification(itemID, "decision"))
        .thenReturn(createdNotification);

    assertDoesNotThrow(() ->
        notificationUCC.createDecisionNotification(itemID, userID)
    );

    Mockito.verify(dalServicesMock, Mockito.times(2))
        .startTransaction();
    Mockito.verify(notificationDAOmock)
        .createNotification(itemID, "decision");
    Mockito.verify(notificationDAOmock)
        .createIndividualNotification(createdNotification.getId(), userID);
    Mockito.verify(dalServicesMock, Mockito.times(2)).commit();
    Mockito.verify(notificationDAOmock)
        .getNotificationIdFromItemId(itemID, "proposal");
    Mockito.verify(notificationDAOmock)
        .deleteAllIndividualNotifications(anyInt());
    Mockito.verify(notificationDAOmock)
        .deleteNotification(anyInt());
  }

  @Test
  public void testDeleteItemNotifications() {
    int itemID = 123;
    int notificationID = 456;
    Mockito.when(notificationDAOmock.getNotificationIdFromItemId(anyInt(), Mockito.anyString()))
        .thenReturn(notificationID);

    assertDoesNotThrow(() ->
        notificationUCC.deleteItemNotifications(itemID)
    );

    Mockito.verify(dalServicesMock).startTransaction();
    Mockito.verify(notificationDAOmock)
        .getNotificationIdFromItemId(itemID, "proposal");
    Mockito.verify(notificationDAOmock)
        .deleteAllIndividualNotifications(notificationID);
    Mockito.verify(notificationDAOmock)
        .deleteNotification(notificationID);
    Mockito.verify(dalServicesMock).commit();
  }

  @Test
  public void testDeleteSingleNotification() {
    int notificationID = 123;
    int userID = 456;

    assertDoesNotThrow(() ->
        notificationUCC.deleteSingleNotification(notificationID, userID)
    );

    Mockito.verify(dalServicesMock, Mockito.times(1))
        .startTransaction();
    Mockito.verify(notificationDAOmock, Mockito.times(1))
        .deleteIndividualNotification(notificationID, userID);
    Mockito.verify(notificationDAOmock, Mockito.times(1))
        .verifyNumberOfIndividualNotifications(notificationID);
    Mockito.verify(dalServicesMock, Mockito.times(1))
        .commit();
  }
}