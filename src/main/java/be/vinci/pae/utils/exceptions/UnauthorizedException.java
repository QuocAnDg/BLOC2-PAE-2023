package be.vinci.pae.utils.exceptions;

/**
 * UnauthorizedException class.
 */
public class UnauthorizedException extends RuntimeException {

  /**
   * Unauthorized exception.
   *
   * @param message exception message
   */
  public UnauthorizedException(String message) {
    super(message);
  }
}
