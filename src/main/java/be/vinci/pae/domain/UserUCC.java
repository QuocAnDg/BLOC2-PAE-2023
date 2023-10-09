package be.vinci.pae.domain;

import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import java.util.List;

/**
 * Interface for UserUCC.
 */
public interface UserUCC {

  /**
   * Get a user associated with an email and check their password with the password entered.
   *
   * @param email    the user's email.
   * @param password the user's hashed password.
   * @return a UserDTO if existing user and correct password ; else null.
   */
  UserDTO login(String email, String password)
      throws FatalException, UnauthorizedException;

  /**
   * Register a user.
   *
   * @param user the user to register.
   * @return a UserDTO objet of the registered user.
   */
  UserDTO register(UserDTO user);

  /**
   * Get a user associated with an id.
   *
   * @param id the user's id.
   * @return a UserDTO if existing user; else null.
   */
  UserDTO findOne(int id) throws FatalException, UnauthorizedException, ElementNotFoundException;

  /**
   * Get all users except the admin.
   *
   * @return a list containing all the users except the admin.
   */
  List<UserDTO> findAll() throws FatalException, NoContentException;

  /**
   * Update the role of a user from "user" to "helper" or "admin".
   *
   * @param id      the user's id.
   * @param newRole the user's new role.
   * @return the User with the updated role.
   */
  UserDTO updateRole(int id, String newRole)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * Edit user's firstname, lastname, email, phone number and photo.
   *
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  UserDTO editUser(UserDTO userToEdit);

  /**
   * Change the user's password.
   *
   * @param id          the user's id.
   * @param oldPassword the user's old password.
   * @param newPassword the user's new password.
   * @return the UserDTO with the new password.
   */
  UserDTO changePassword(int id, String oldPassword, String newPassword);

  /**
   * find all helpers and admins.
   *
   * @return a list of all helpers and admins.
   */
  List<UserDTO> findAllHelpersAndAdmin() throws FatalException, NoContentException;
}


