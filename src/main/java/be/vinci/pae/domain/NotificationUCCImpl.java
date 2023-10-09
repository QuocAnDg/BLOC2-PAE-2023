package be.vinci.pae.domain;

import be.vinci.pae.services.NotificationDAO;
import be.vinci.pae.services.utils.DALServices;
import jakarta.inject.Inject;
import java.util.List;

/**
 * NotificationUCC interface implementation.
 */
public class NotificationUCCImpl implements NotificationUCC {
  @Inject
  private NotificationDAO notificationDAO;
  @Inject
  private UserUCC userUCC;
  @Inject
  private DALServices myDalServices;

  /**
   * view the notifications list of a user.
   *
   * @param id the user's id
   * @return a list of NotificationDTO; null if the list is empty.
   */
  @Override
  public List<NotificationDTO> viewAllNotificationsOfUser(int id) {
    List<NotificationDTO> notifications;
    try {
      myDalServices.startTransaction();
      notifications = notificationDAO.getNotificationsOfUser(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }

    return notifications;
  }

  /**
   * mark all notifications of the given user as read.
   *
   * @param userID the user's id
   */
  @Override
  public void markAsRead(int userID) {
    try {
      myDalServices.startTransaction();
      notificationDAO.markAsRead(userID);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
  }

  /**
   * create a proposal notification for all the helpers and admin.
   *
   * @param itemID the proposed item's id
   */
  @Override
  public void createProposalNotification(int itemID) {
    List<UserDTO> helpersList = userUCC.findAllHelpersAndAdmin();
    try {
      myDalServices.startTransaction();
      NotificationDTO createdNotification = notificationDAO.createNotification(itemID, "proposal");
      for (UserDTO helper : helpersList) {
        notificationDAO.createIndividualNotification(createdNotification.getId(), helper.getId());
      }
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
  }

  /**
   * create a decision notification for the owner of the item.
   *
   * @param itemID the proposed item's id
   * @param userID the owner's id
   */
  @Override
  public void createDecisionNotification(int itemID, int userID) {
    try {
      myDalServices.startTransaction();
      NotificationDTO createdNotification = notificationDAO.createNotification(itemID, "decision");
      notificationDAO.createIndividualNotification(createdNotification.getId(), userID);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    deleteItemNotifications(itemID);
  }

  /**
   * delete all the notifications of the given notification id.
   *
   * @param itemID the notification's id
   */
  @Override
  public void deleteItemNotifications(int itemID) {
    try {
      myDalServices.startTransaction();
      int notificationID = notificationDAO.getNotificationIdFromItemId(itemID, "proposal");
      if (notificationID != -1) {
        notificationDAO.deleteAllIndividualNotifications(notificationID);
        notificationDAO.deleteNotification(notificationID);
      }
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
  }

  /**
   * delete a single notification of the given notification id and the given user id.
   *
   * @param notificationID the notification's id
   * @param userID         the user's id
   */
  @Override
  public void deleteSingleNotification(int notificationID, int userID) {
    try {
      myDalServices.startTransaction();
      notificationDAO.deleteIndividualNotification(notificationID, userID);
      notificationDAO.verifyNumberOfIndividualNotifications(notificationID);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
  }
}
