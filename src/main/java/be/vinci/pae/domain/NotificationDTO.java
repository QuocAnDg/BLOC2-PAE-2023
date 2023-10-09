package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * NotificationDTO interface containing only getters and setters of a Notification.
 */
public interface NotificationDTO {
  /**
   * Get the id of the notification.
   *
   * @return the id of the notification
   */
  int getId();

  /**
   * Set the id of the notification.
   *
   * @param id the id of the notification
   */
  void setId(int id);

  /**
   * Get the type of the notification.
   *
   * @return the type of the notification
   */
  String getType();

  /**
   * Set the type of the notification.
   *
   * @param type the type of the notification
   */
  void setType(String type);

  /**
   * Get the id of the item linked to the notification.
   *
   * @return the id of the item linked to the notification
   */
  int getItemID();

  /**
   * Set the id of the item linked to the notification.
   *
   * @param itemID the id of the item linked to the notification
   */
  void setItemID(int itemID);

  /**
   * Get the name of the item linked to the notification.
   *
   * @return the name of the item linked to the notification
   */
  String getItemName();

  /**
   * Set the name of the item linked to the notification.
   *
   * @param itemName the name of the item linked to the notification
   */
  void setItemName(String itemName);

  /**
   * Get the creation date of the notification.
   *
   * @return the creation date of the notification
   */
  LocalDate getCreationDate();

  /**
   * Set the creation date of the notification.
   *
   * @param creationDate the creation date of the notification
   */
  void setCreationDate(LocalDate creationDate);

  /**
   * Get the read status of the notification.
   *
   * @return the read status of the notification
   */
  boolean getIsRead();

  /**
   * Set the read status of the notification.
   *
   * @param isRead the read status of the notification
   */
  void setIsRead(boolean isRead);

  /**
   * Get the state of the item linked to the notification.
   *
   * @return the state of the item linked to the notification
   */
  String getItemState();

  /**
   * Set the state of the item linked to the notification.
   *
   * @param state the state of the item linked to the notification
   */
  void setItemState(String state);
}
