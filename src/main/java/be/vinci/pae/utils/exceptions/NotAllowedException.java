package be.vinci.pae.utils.exceptions;

/**
 * NotAllowedException class.
 */
public class NotAllowedException extends RuntimeException {

  /**
   * Not allowed exception.
   *
   * @param message exception message
   */
  public NotAllowedException(String message) {
    super(message);
  }
}
