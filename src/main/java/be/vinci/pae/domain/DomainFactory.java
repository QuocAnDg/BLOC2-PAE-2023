package be.vinci.pae.domain;

/**
 * DomainFactory interface.
 */
public interface DomainFactory {

  /**
   * Creates and returns a UserDTO object.
   *
   * @return a new UserDTO object.
   */
  UserDTO getUserDTO();

  /**
   * Creates and returns an ItemDTO object.
   *
   * @return a new ItemDTO object.
   */

  ItemDTO getItemDTO();

  /**
   * Creates and returns a PhotoDTO object.
   *
   * @return a new PhotoDTO object.
   */

  PhotoDTO getPhotoDTO();

  /**
   * Creates and returns a AvailabilityDTO object.
   *
   * @return a new AvailabilityDTO object.
   */

  AvailabilityDTO getAvailabilityDTO();

  /**
   * Creates and returns a NotificationDTO object.
   *
   * @return a new NotificationDTO object.
   */

  NotificationDTO getNotificationDTO();
}
