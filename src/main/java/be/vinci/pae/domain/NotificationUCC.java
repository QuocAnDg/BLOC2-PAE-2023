package be.vinci.pae.domain;

import java.util.List;

/**
 * NotificationUCC interface.
 */
public interface NotificationUCC {
  /**
   * view the notifications list of a user.
   *
   * @param id the user's id
   * @return a list of NotificationDTO; null if the list is empty.
   */
  List<NotificationDTO> viewAllNotificationsOfUser(int id);

  /**
   * mark all notifications of the given user as read.
   *
   * @param userID the user's id
   */
  void markAsRead(int userID);

  /**
   * create a proposal notification for all the helpers and admin.
   *
   * @param itemID the proposed item's id
   */
  void createProposalNotification(int itemID);

  /**
   * create a decision notification for the owner of the item.
   *
   * @param itemID the proposed item's id
   * @param userID the owner's id
   */
  void createDecisionNotification(int itemID, int userID);

  /**
   * delete all the notifications of the given notification id.
   *
   * @param notificationID the notification's id
   */
  void deleteItemNotifications(int notificationID);

  /**
   * delete all the notifications of the given item and the given type.
   *
   * @param notificationID the notification's id
   * @param userID         the user's id
   */
  void deleteSingleNotification(int notificationID, int userID);
}
