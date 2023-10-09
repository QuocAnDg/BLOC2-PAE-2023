package be.vinci.pae.services;

import be.vinci.pae.domain.UserDTO;
import java.util.List;

/**
 * UserDAO interface.
 */
public interface UserDAO {

  /**
   * Find in the database a user associated with a given email.
   *
   * @param email the user's email.
   * @return the user associated with the address email if found; null else.
   */
  UserDTO getOneUserByEmail(String email);

  /**
   * Find in the database a user associated with a given id.
   *
   * @param id the user's id.
   * @return the user associated with the id if found; null else.
   */
  UserDTO getOneUserById(int id);

  /**
   * Add one user.
   *
   * @param user the user to be added.
   * @return the user added.
   */
  UserDTO addOneUser(UserDTO user);


  /**
   * Get all users except the admin.
   *
   * @return a list containing all the users except the admin.
   */
  List<UserDTO> getAllUsers();

  /**
   * Get all helpers and admin.
   *
   * @return a list containing all the helpers and admin.
   */
  List<UserDTO> getAllHelpersAndAdmin();

  /**
   * Find in the database a user associated with a given id then, update the role of a user from
   * "user" to "helper" or "admin".
   *
   * @param id      the user's id.
   * @param newRole the user's new role.
   * @return the User with the updated role.
   */
  UserDTO updateRole(int id, String newRole);

  /**
   * Edit user's firstname, lastname, email, phone number.
   *
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  UserDTO editUser(UserDTO userToEdit);

  /**
   * Change the user's password.
   *
   * @param userDTO the user to change its password.
   * @return the UserDTO with the new password.
   */
  UserDTO changePassword(UserDTO userDTO);

  /**
   * Edit user's firstname, lastname, email, phone number and photo.
   *
   * @param idPhoto    the photo's id.
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  UserDTO editUserWithPhoto(int idPhoto, UserDTO userToEdit);
}
