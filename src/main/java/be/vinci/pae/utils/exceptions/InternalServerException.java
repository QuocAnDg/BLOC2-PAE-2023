package be.vinci.pae.utils.exceptions;

/**
 * InternalServerException class.
 */
public class InternalServerException extends RuntimeException {

  /**
   * Internal server exception.
   *
   * @param message exception message
   */
  public InternalServerException(String message) {
    super(message);
  }
}
