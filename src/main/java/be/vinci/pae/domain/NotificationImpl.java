package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * Notification interface implementation.
 */
public class NotificationImpl implements Notification {

  private int id;
  private String type;
  private int itemID;
  private String itemName;
  private LocalDate creationDate;
  private boolean isRead;
  private String state;

  public NotificationImpl() {}

  /**
   * Get the id of the notification.
   *
   * @return the id of the notification
   */
  @Override
  public int getId() {
    return id;
  }

  /**
   * Set the id of the notification.
   *
   * @param id the id of the notification
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Get the type of the notification.
   *
   * @return the type of the notification
   */
  @Override
  public String getType() {
    return type;
  }

  /**
   * Set the type of the notification.
   *
   * @param type the type of the notification
   */
  @Override
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get the id of the item linked to the notification.
   *
   * @return the id of the item linked to the notification
   */
  @Override
  public int getItemID() {
    return itemID;
  }

  /**
   * Set the id of the item linked to the notification.
   *
   * @param itemID the id of the item linked to the notification
   */
  @Override
  public void setItemID(int itemID) {
    this.itemID = itemID;
  }

  /**
   * Get the name of the item linked to the notification.
   *
   * @return the name of the item linked to the notification
   */
  @Override
  public String getItemName() {
    return itemName;
  }

  /**
   * Set the name of the item linked to the notification.
   *
   * @param itemName the name of the item linked to the notification
   */
  @Override
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  /**
   * Get the creation date of the notification.
   *
   * @return the creation date of the notification
   */
  @Override
  public LocalDate getCreationDate() {
    return creationDate;
  }

  /**
   * Set the creation date of the notification.
   *
   * @param creationDate the creation date of the notification
   */
  @Override
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Get the read status of the notification.
   *
   * @return the read status of the notification
   */
  @Override
  public boolean getIsRead() {
    return isRead;
  }

  /**
   * Set the read status of the notification.
   *
   * @param isRead the read status of the notification
   */
  @Override
  public void setIsRead(boolean isRead) {
    this.isRead = isRead;
  }

  /**
   * Get the state of the item linked to the notification.
   *
   * @return the state of the item linked to the notification
   */
  @Override
  public String getItemState() {
    return state;
  }

  /**
   * Set the state of the item linked to the notification.
   *
   * @param state the state of the item linked to the notification
   */
  @Override
  public void setItemState(String state) {
    this.state = state;
  }
}
