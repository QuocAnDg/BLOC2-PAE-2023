package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * UserDTO interface containing only getters and setters of a User.
 */
public interface UserDTO {

  /**
   * Get the user's id.
   *
   * @return the user's id.
   */
  int getId();

  /**
   * Set the user's id.
   *
   * @param id id to set.
   */
  void setId(int id);

  /**
   * Get the user's lastname.
   *
   * @return the user's lastname.
   */
  String getLastname();

  /**
   * Set the user's lastname.
   *
   * @param lastname lastname to set.
   */
  void setLastname(String lastname);

  /**
   * Get the user's firstname.
   *
   * @return the user's firstname.
   */
  String getFirstname();

  /**
   * Set the user's firstname.
   *
   * @param firstname firstname to set.
   */
  void setFirstname(String firstname);

  /**
   * Get the user's email.
   *
   * @return the user's email.
   */
  String getEmail();

  /**
   * Set the user's email.
   *
   * @param email email to set.
   */
  void setEmail(String email);

  /**
   * Get the user's password.
   *
   * @return the user's password.
   */
  String getPassword();

  /**
   * Set the user's password.
   *
   * @param password password to set.
   */
  void setPassword(String password);

  /**
   * Get the user's phone number.
   *
   * @return the user's phone number.
   */
  String getPhoneNumber();

  /**
   * Set the user's phone number.
   *
   * @param phoneNumber phone number to set.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the user's profile photo.
   *
   * @return the user's profile photo.
   */
  PhotoDTO getProfilePhoto();

  /**
   * Set user's profile photo.
   *
   * @param profilePhoto profile photo to set.
   */
  void setProfilePhoto(PhotoDTO profilePhoto);

  /**
   * Get user's role.
   *
   * @return the user's role.
   */
  String getRole();

  /**
   * Set the user's role.
   *
   * @param role role to set.
   */
  void setRole(String role);

  /**
   * Get the user's register date.
   *
   * @return a LocalDate that represents the user's register date.
   */
  LocalDate getRegisterDate();

  /**
   * Set the user's register date.
   *
   * @param registerDate register date to set.
   */
  void setRegisterDate(LocalDate registerDate);

  /**
   * Get the user's version.
   *
   * @return an int corresponding to the user's object version.
   */
  int getVersion();

  /**
   * Set the user selling date.
   *
   * @param version version number to set.
   */
  void setVersion(int version);
}
