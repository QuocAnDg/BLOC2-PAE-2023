package be.vinci.pae.services;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDAO implementation.
 */
public class NotificationDAOImpl implements NotificationDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private DomainFactory domainFactory;

  /**
   * get a NotificationDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO containing all information about the notification;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  @Override
  public NotificationDTO getNotificationDTOFromPreparedStatement(PreparedStatement ps)
      throws SQLException {
    NotificationDTO notificationDTO = null;
    try (ResultSet rs = ps.executeQuery()) {
      if (rs.next()) {
        notificationDTO = domainFactory.getNotificationDTO();
        setNotificationFromResultSet(rs, notificationDTO);
      }
    }
    return notificationDTO;
  }

  /**
   * get a NotificationDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO containing all information about the notification;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  @Override
  public NotificationDTO getIncompleteNotificationDTOFromPreparedStatement(PreparedStatement ps)
      throws SQLException {
    NotificationDTO notificationDTO = null;
    try (ResultSet rs = ps.executeQuery()) {
      if (rs.next()) {
        notificationDTO = domainFactory.getNotificationDTO();
        setIncompleteNotificationFromResultSet(rs, notificationDTO);
      }
    }
    return notificationDTO;
  }

  /**
   * get a NotificationDTO list from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a NotificationDTO list containing all information about notifications;
   *     null if no notifications found;
   * @throws SQLException if an error occurs while getting the data from the result set
   */
  @Override
  public List<NotificationDTO> getNotificationDTOListFromPreparedStatement(PreparedStatement ps)
      throws SQLException {
    List<NotificationDTO> notificationDTOList = new ArrayList<>();
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        NotificationDTO notification = domainFactory.getNotificationDTO();
        setNotificationFromResultSet(rs, notification);
        notificationDTOList.add(notification);
      }
      return notificationDTOList;
    }
  }

  /**
   * set the notificationDTO attributes with the resultset query.
   *
   * @param rs           a Resultset
   * @param notification a NotificationDTO
   * @throws SQLException if an error occurs while getting the data from the resultset
   */
  @Override
  public void setNotificationFromResultSet(ResultSet rs, NotificationDTO notification)
      throws SQLException {
    notification.setId(rs.getInt("notification_id"));
    notification.setItemID(rs.getInt("concerned_item"));
    notification.setItemName(rs.getString("name"));
    notification.setType(rs.getString("type"));
    notification.setCreationDate(LocalDate.parse(rs.getString("creation_date")));
    notification.setIsRead(rs.getBoolean("isread"));
    notification.setItemState(rs.getString("state"));
  }

  /**
   * set the notificationDTO attributes with the resultset query.
   *
   * @param rs           a Resultset
   * @param notification a NotificationDTO
   * @throws SQLException if an error occurs while getting the data from the resultset
   */
  @Override
  public void setIncompleteNotificationFromResultSet(ResultSet rs, NotificationDTO notification)
      throws SQLException {
    notification.setId(rs.getInt("notification_id"));
    notification.setItemID(rs.getInt("concerned_item"));
    notification.setType(rs.getString("type"));
    notification.setCreationDate(LocalDate.parse(rs.getString("creation_date")));
  }

  /**
   * Get in the database the notifications matching the given user id.
   *
   * @param userID the user id
   * @return a List of NotificationDTO.
   */
  @Override
  public List<NotificationDTO> getNotificationsOfUser(int userID) {
    String sql = """
        SELECT n.notification_id, n.concerned_item, n.type, n.creation_date, i_n.isread,
        i.name, i.state
        FROM pae.notifications n, pae.individual_notifications i_n, pae.items i
        WHERE n.notification_id = i_n.notification_id
        AND n.concerned_item = i.item_id
        AND i_n.concerned_user = ?
        ORDER BY n.creation_date DESC;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, userID);
      return getNotificationDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * create a notification of the given type in the database.
   *
   * @param itemID the item id
   * @param type   the type of the notification
   * @return a NotificationDTO containing all information about the notification
   */
  @Override
  public NotificationDTO createNotification(int itemID, String type) {
    String sql = """
        INSERT INTO pae.notifications
        VALUES (DEFAULT, ?, ?, ?)
        RETURNING *;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, itemID);
      ps.setString(2, type);
      ps.setDate(3, Date.valueOf(LocalDate.now()));
      return getIncompleteNotificationDTOFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database the notification id matching the given item id
   * and the given notification type.
   *
   * @param itemID the item id
   * @param type   the type of the notification
   * @return the notification id
   */
  @Override
  public int getNotificationIdFromItemId(int itemID, String type) {
    int notificationID = -1;
    String sql = """
        SELECT notification_id
        FROM pae.notifications
        WHERE concerned_item = ?
        AND type = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, itemID);
      ps.setString(2, type);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          notificationID = rs.getInt("notification_id");
        }
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }

    return notificationID;
  }

  /**
   * create an individual notification in the database.
   *
   * @param notificationID the item id
   * @param userID         the user id
   */
  @Override
  public void createIndividualNotification(int notificationID, int userID) {
    String sql = """
        INSERT INTO pae.individual_notifications
        VALUES (?, ?, false);
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, notificationID);
      ps.setInt(2, userID);
      ps.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Delete in the database the notification corresponding to the given notification id.
   *
   * @param notificationID the notification id
   */
  @Override
  public void deleteNotification(int notificationID) {
    String sql = """
        DELETE FROM pae.notifications n
        WHERE n.notification_id = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, notificationID);
      ps.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Delete in the database all the individual notifications corresponding
   * to the given notification id.
   *
   * @param notificationID the notification id
   */
  @Override
  public void deleteAllIndividualNotifications(int notificationID) {
    String sql = """
        DELETE FROM pae.individual_notifications i_n
        WHERE i_n.notification_id = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, notificationID);
      ps.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Delete in the database the individual notification corresponding
   * to the given notification id and the given user id.
   *
   * @param notificationID the notification id
   * @param userID         the user id
   */
  @Override
  public void deleteIndividualNotification(int notificationID, int userID) {
    String sql = """
        DELETE FROM pae.individual_notifications i_n
        WHERE i_n.notification_id = ?
        AND i_n.concerned_user = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, notificationID);
      ps.setInt(2, userID);
      ps.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * mark all notifications of the given user as read.
   *
   * @param userID the user id
   */
  @Override
  public void markAsRead(int userID) {
    String sql = """
        UPDATE pae.individual_notifications
        SET isread = true
        WHERE concerned_user = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, userID);
      ps.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database the number of individual notifications
   * matching the given notification id.
   *
   * @param notificationID the notification id
   * @return the number of individual notifications
   */
  @Override
  public int getNumberOfIndividualNotifications(int notificationID) {
    String sql = """
        SELECT COUNT(*)
        FROM pae.individual_notifications i_n
        WHERE i_n.notification_id = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, notificationID);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * verify the number of individual notifications matching the given notification id
   * and delete the notification if there is no individual notification left.
   *
   * @param notificationID the notification id
   */
  @Override
  public void verifyNumberOfIndividualNotifications(int notificationID) {
    if (getNumberOfIndividualNotifications(notificationID) == 0) {
      deleteNotification(notificationID);
    }
  }
}
