package be.vinci.pae.utils.exceptions;

/**
 * NoContentException class.
 */
public class NoContentException extends RuntimeException {

  /**
   * Element not found exception.
   *
   * @param message exception message
   */
  public NoContentException(String message) {
    super(message);
  }
}
