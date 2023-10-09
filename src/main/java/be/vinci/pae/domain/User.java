package be.vinci.pae.domain;

/**
 * User interface inheriting the UserDTO interface and containing business methods.
 */
public interface User extends UserDTO {

  /**
   * Compare a raw password with a User's encrypted password.
   *
   * @param password encrypted password.
   * @return true if the passwords are matching; else false.
   */
  boolean checkPassword(String password);


  /**
   * Hashes user's password.
   */
  void hashPassword();

}
