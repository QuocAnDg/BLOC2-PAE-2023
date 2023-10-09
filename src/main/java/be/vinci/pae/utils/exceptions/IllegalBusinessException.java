package be.vinci.pae.utils.exceptions;

/**
 * Class for business related exceptions.
 */
public class IllegalBusinessException extends RuntimeException {

  /**
   * Illegal Business exception.
   *
   * @param cause Throwable cause
   */
  public IllegalBusinessException(Throwable cause) {
    super(cause);
  }
}
