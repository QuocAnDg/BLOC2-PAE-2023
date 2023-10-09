package be.vinci.pae.utils.exceptions;

/**
 * BadRequestException class.
 */
public class BadRequestException extends RuntimeException {

  /**
   * Bad request exception.
   *
   * @param message exception message
   */
  public BadRequestException(String message) {
    super(message);
  }
}
