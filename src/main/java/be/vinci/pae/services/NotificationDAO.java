package be.vinci.pae.services;

import be.vinci.pae.domain.NotificationDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * NotificationDAO interface.
 */
public interface NotificationDAO {
  /**
   * get a NotificationDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO containing all information about the notification;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  NotificationDTO getNotificationDTOFromPreparedStatement(PreparedStatement ps)
      throws SQLException;

  /**
   * get a NotificationDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO containing all information about the notification;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  NotificationDTO getIncompleteNotificationDTOFromPreparedStatement(PreparedStatement ps)
      throws SQLException;

  /**
   * get a NotificationDTO list from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO list containing all information about notifications;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  List<NotificationDTO> getNotificationDTOListFromPreparedStatement(PreparedStatement ps)
      throws SQLException;

  /**
   * set the notificationDTO attributes with the resultset query.
   *
   * @param rs           a Resultset
   * @param notification a NotificationDTO
   * @throws SQLException if an error occurs while getting the data from the resultset
   */
  void setNotificationFromResultSet(ResultSet rs, NotificationDTO notification)
      throws SQLException;

  /**
   * set the notificationDTO attributes with the resultset query.
   *
   * @param rs           a Resultset
   * @param notification a NotificationDTO
   * @throws SQLException if an error occurs while getting the data from the resultset
   */
  void setIncompleteNotificationFromResultSet(ResultSet rs, NotificationDTO notification)
      throws SQLException;

  /**
   * Get in the database the notifications matching the given user id.
   *
   * @param userID the user id
   * @return a List of NotificationDTO.
   */
  List<NotificationDTO> getNotificationsOfUser(int userID);

  /**
   * create a notification of the given type in the database.
   *
   * @param itemID the item id
   * @param type   the type of the notification
   * @return a NotificationDTO containing all information about the notification
   */
  NotificationDTO createNotification(int itemID, String type);

  /**
   * Get in the database the notification id matching the given item id
   * and the given notification type.
   *
   * @param itemID the item id
   * @param type   the type of the notification
   * @return the notification id
   */
  int getNotificationIdFromItemId(int itemID, String type);

  /**
   * create an individual notification in the database.
   *
   * @param notificationID the item id
   * @param userID         the user id
   */
  void createIndividualNotification(int notificationID, int userID);

  /**
   * Delete in the database the notification corresponding to the given notification id.
   *
   * @param notificationID the notification id
   */
  void deleteNotification(int notificationID);

  /**
   * Delete in the database all the individual notifications
   * corresponding to the given notification id.
   *
   * @param notificationID the notification id
   */
  void deleteAllIndividualNotifications(int notificationID);

  /**
   * Delete in the database the individual notification corresponding
   * to the given notification id and the given user id.
   *
   * @param notificationID the notification id
   * @param userID         the user id
   */
  void deleteIndividualNotification(int notificationID, int userID);

  /**
   * mark all notifications of the given user as read.
   *
   * @param userID the user id
   */
  void markAsRead(int userID);

  /**
   * Get in the database the number of individual notifications
   * matching the given notification id.
   *
   * @param notificationID the notification id
   * @return the number of individual notifications
   */
  int getNumberOfIndividualNotifications(int notificationID);

  /**
   * verify the number of individual notifications matching the given notification id
   * and delete the notification if there is no individual notification left.
   *
   * @param notificationID the notification id
   */
  void verifyNumberOfIndividualNotifications(int notificationID);
}
