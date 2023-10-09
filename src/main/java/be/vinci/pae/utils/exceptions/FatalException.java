package be.vinci.pae.utils.exceptions;

/**
 * FatalException class.
 */
public class FatalException extends RuntimeException {

  /**
   * Fatal exception.
   *
   * @param cause Throwable cause
   */
  public FatalException(Throwable cause) {
    super(cause);
  }
}
