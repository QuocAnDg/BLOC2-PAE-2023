package be.vinci.pae.domain;


/**
 * Implementation of domainFactory.
 */
public class DomainFactoryImpl implements DomainFactory {

  /**
   * Creates and returns a UserDTO object.
   *
   * @return a new UserDTO object.
   */
  @Override
  public UserDTO getUserDTO() {
    return new UserImpl();
  }

  /**
   * Creates and returns an ItemDTO object.
   *
   * @return a new ItemDTO object.
   */
  @Override
  public ItemDTO getItemDTO() {
    return new ItemImpl();
  }

  /**
   * Creates and returns a PhotoDTO object.
   *
   * @return a new PhotoDTO object.
   */
  @Override
  public PhotoDTO getPhotoDTO() {
    return new PhotoImpl();
  }

  /**
   * Creates and returns a AvailabilityDTO object.
   *
   * @return a new AvailabilityDTO object.
   */
  @Override
  public AvailabilityDTO getAvailabilityDTO() {
    return new AvailabilityImpl();
  }

  /**
   * Creates and returns a NotificationDTO object.
   *
   * @return a new NotificationDTO object.
   */

  public NotificationDTO getNotificationDTO() {
    return new NotificationImpl();
  }
}
