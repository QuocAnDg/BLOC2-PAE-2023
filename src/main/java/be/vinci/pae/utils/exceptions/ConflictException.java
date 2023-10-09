package be.vinci.pae.utils.exceptions;

/**
 * ConflictException class.
 */
public class ConflictException extends RuntimeException {

  /**
   * Conflict exception. This exception is thrown when the state of the client and the server are
   * different.
   *
   * @param message exception message
   */
  public ConflictException(String message) {
    super(message);
  }
}
