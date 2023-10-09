package be.vinci.pae.utils.exceptions;

/**
 * ElementNotFoundException class.
 */
public class ElementNotFoundException extends RuntimeException {

  /**
   * Element not found exception.
   *
   * @param message exception message
   */
  public ElementNotFoundException(String message) {
    super(message);
  }
}
