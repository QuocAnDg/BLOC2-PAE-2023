package be.vinci.pae.domain;

import be.vinci.pae.services.UserDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Implementation of UserUCC.
 */
public class UserUCCImpl implements UserUCC {

  @Inject
  private UserDAO myUserDAO;

  @Inject
  private DALServices myDalServices;

  /**
   * Get a user associated with an email and check their password with the password entered.
   *
   * @param email    the user's email.
   * @param password the user's hashed password.
   * @return a UserDTO if existing user and correct password ; else null.
   */
  @Override
  public UserDTO login(String email, String password) {
    User user;
    try {
      myDalServices.startTransaction();
      user = (User) myUserDAO.getOneUserByEmail(email);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (user == null || !user.checkPassword(password)) {
      throw new IllegalBusinessException(
          new UnauthorizedException("User not found or the password is incorrect")
      );
    }
    return user;
  }

  /**
   * Register a user.
   *
   * @param user the user to register.
   * @return a UserDTO objet of the registered user.
   */
  @Override
  public UserDTO register(UserDTO user) {
    ((User) user).hashPassword();
    User userCreated;

    try {
      myDalServices.startTransaction();
      if (myUserDAO.getOneUserByEmail(user.getEmail()) != null) {
        throw new IllegalBusinessException(
            new NotAllowedException("Email already in database")
        );
      }
      userCreated = (User) myUserDAO.addOneUser(user);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (userCreated == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Failed register"));
    }
    return userCreated;
  }

  /**
   * Get a user associated with an id.
   *
   * @param id the user's id.
   * @return a UserDTO if existing user; else null.
   */
  @Override
  public UserDTO findOne(int id) {
    UserDTO user;
    try {
      myDalServices.startTransaction();
      user = myUserDAO.getOneUserById(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (user == null) {
      throw new IllegalBusinessException(
          new ElementNotFoundException("User not found"));
    }
    return user;
  }

  /**
   * Get all users.
   *
   * @return a list containing all the users.
   */
  @Override
  public List<UserDTO> findAll() {
    List<User> users;
    try {
      myDalServices.startTransaction();
      users = myUserDAO.getAllUsers()
          .stream()
          .map(userDTO -> (User) userDTO)
          .toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (users.isEmpty()) {
      throw new IllegalBusinessException(
          new NoContentException("Users not found"));
    }
    return users
        .stream()
        .map(user -> (UserDTO) user)
        .toList();
  }

  /**
   * Update the role of a user from "user" to "helper" or "admin".
   *
   * @param id the user's id.
   * @return the User with the updated role. null if user not found.
   */
  @Override
  public UserDTO updateRole(int id, String newRole) {
    UserDTO userToUpdate = findOne(id);
    if (userToUpdate.getRole().equals("admin")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Not possible to update an admin."));
    }
    if (userToUpdate.getRole().equals(newRole)) {
      throw new IllegalBusinessException(
          new NotAllowedException("User already has this role.")
      );
    }
    UserDTO updatedUser;
    try {
      myDalServices.startTransaction();
      updatedUser = myUserDAO.updateRole(id, newRole);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (updatedUser == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Failed updateRole"));
    }
    return updatedUser;
  }

  /**
   * Edit user's firstname, lastname, email, phone number and photo.
   *
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  @Override
  public UserDTO editUser(UserDTO userToEdit) {
    UserDTO editedUser;
    try {
      myDalServices.startTransaction();
      UserDTO userFoundId = myUserDAO.getOneUserById(userToEdit.getId());
      if (userFoundId == null) {
        throw new IllegalBusinessException(
            new NotAllowedException("User not found in database")
        );
      }
      UserDTO userFoundEmail = myUserDAO.getOneUserByEmail(userToEdit.getEmail());
      if (userFoundEmail != null && userFoundId.getId() != userFoundEmail.getId()) {
        throw new IllegalBusinessException(
            new NotAllowedException("Email already exists in database")
        );
      }
      if (userToEdit.getProfilePhoto() == null) {
        editedUser = myUserDAO.editUser(userToEdit);
      } else {
        editedUser = myUserDAO.editUserWithPhoto(userToEdit.getProfilePhoto().getId(), userToEdit);
      }
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (editedUser == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Failed edit"));
    }
    return editedUser;
  }

  /**
   * Change the user's password.
   *
   * @param id          the user's id.
   * @param oldPassword the user's old password.
   * @param newPassword the user's new password.
   * @return the UserDTO with the new password.
   */
  @Override
  public UserDTO changePassword(int id, String oldPassword, String newPassword) {

    UserDTO userOldPassword = findOne(id);
    if (!((User) userOldPassword).checkPassword(oldPassword)) {
      throw new IllegalBusinessException(
          new UnauthorizedException("User not found or the password is incorrect")
      );
    }
    userOldPassword.setPassword(newPassword);
    ((User) userOldPassword).hashPassword();

    UserDTO userNewPassword;
    try {
      myDalServices.startTransaction();
      userNewPassword = myUserDAO.changePassword(userOldPassword);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (userNewPassword == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Failed to change password"));
    }
    return userNewPassword;
  }

  /**
   * Get all the helpers or admin.
   *
   * @return a list containing all the users with the role "helper" or "admin".
   */
  public List<UserDTO> findAllHelpersAndAdmin() {
    List<UserDTO> users;

    try {
      myDalServices.startTransaction();
      users = myUserDAO.getAllHelpersAndAdmin();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }

    if (users.isEmpty()) {
      throw new IllegalBusinessException(
          new NoContentException("No users found"));
    }

    return users;
  }
}
