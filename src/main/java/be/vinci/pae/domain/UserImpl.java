package be.vinci.pae.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementation of User.
 */
class UserImpl implements User {

  private int id;
  private String lastname;
  private String firstname;
  private String email;
  @JsonIgnore
  private String password;
  private String phoneNumber;
  private PhotoDTO profilePhoto;
  private String role;
  private LocalDate registerDate;
  private int version;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getLastname() {
    return lastname;
  }

  @Override
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  @Override
  public String getFirstname() {
    return firstname;
  }

  @Override
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public PhotoDTO getProfilePhoto() {
    return profilePhoto;
  }

  @Override
  public void setProfilePhoto(PhotoDTO profilePhoto) {
    this.profilePhoto = profilePhoto;
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public LocalDate getRegisterDate() {
    return registerDate;
  }

  @Override
  public void setRegisterDate(LocalDate registerDate) {
    this.registerDate = registerDate;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Compare a raw password with a User's encrypted password.
   *
   * @param password encrypted password.
   * @return true if the passwords are matching; else false.
   */
  @Override
  public boolean checkPassword(String password) {
    return BCrypt.checkpw(password, this.password);
  }


  /**
   * Hashes user's password.
   */
  @Override
  public void hashPassword() {
    this.password = BCrypt.hashpw(password, BCrypt.gensalt());
  }


  @Override
  public String toString() {
    return "id : " + id
        + ", lastname='" + lastname + '\''
        + ", firstname='" + firstname + '\''
        + ", email='" + email + '\''
        + ", password='" + password + '\''
        + ", phoneNumber='" + phoneNumber + '\''
        + ", profilePhoto=" + profilePhoto.getAccessPath()
        + ", role=" + role
        + ", registerDate=" + registerDate
        + '}';
  }
}
