package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.User;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.domain.UserUCCImpl;
import be.vinci.pae.services.UserDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * UserUCCTest class.
 */
public class UserUCCTest {


  private static UserDAO userDAOmock;
  private static DALServices dalServicesMock;
  private static ServiceLocator locator;
  private UserUCC userUCC;
  private DomainFactory domainFactory;
  private UserDTO existingUser;

  @BeforeAll
  static void setupBeforeAll() {
    userDAOmock = Mockito.mock(UserDAO.class);
    dalServicesMock = Mockito.mock(DALServices.class);
    AbstractBinder testApplicationBinder = new ApplicationBinder() {
      @Override
      protected void configure() {
        bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
        bind(userDAOmock).to(UserDAO.class);
        bind(dalServicesMock).to(DALServices.class);
        bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
      }
    };
    locator = ServiceLocatorUtilities.bind(testApplicationBinder);
  }

  @BeforeEach
  void setup() {
    userUCC = locator.getService(UserUCC.class);
    domainFactory = locator.getService(DomainFactory.class);
  }

  @AfterEach
  void tearDown() {
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commit();
    Mockito.doNothing().when(dalServicesMock).rollback();
    Mockito.reset(userDAOmock);
    Mockito.reset(dalServicesMock);
  }

  @Test
  @DisplayName("Test login method with no existing user")
  public void testLoginNoExistingUser() {
    Mockito.when(userDAOmock.getOneUserByEmail("nouser@gmail.com")).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class,
        () -> userUCC.login("nouser@gmail.com", "azerty"));

    Throwable cause = parentException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test login method with existing user but incorrect password")
  public void testLoginWithIncorrectPassword() {
    UserDTO existingUser2 = domainFactory.getUserDTO();
    existingUser2.setId(1);
    existingUser2.setPassword(
        "$2a$10$E8S4ry5ACSN0Fz2hLjkmpuU5JP/Ca0VtMwOfCET/ij2.cMVCz4GBu");
    Mockito.when(userDAOmock.getOneUserByEmail("test@gmail.com"))
        .thenReturn(existingUser2);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class,
        () -> userUCC.login("test@gmail.com", "azerty"));

    Throwable cause = parentException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test login method with existing user and correct password")
  public void testLoginWithCorrectPassword() {
    existingUser = domainFactory.getUserDTO();
    existingUser.setId(1);
    existingUser.setPassword(
        "$2a$10$E8S4ry5ACSN0Fz2hLjkmpuU5JP/Ca0VtMwOfCET/ij2.cMVCz4GBu");
    Mockito.when(userDAOmock.getOneUserByEmail("test@gmail.com"))
        .thenReturn(existingUser);
    UserDTO userReturned = userUCC.login("test@gmail.com", "abc");
    assertAll(
        () -> assertNotNull(userReturned, "No existing user or incorrect password"),
        () -> assertEquals(userReturned.getId(), 1)
    );
  }

  @Test
  @DisplayName("Test login method when dalServices startTransaction method throws FatalException")
  public void testLoginExceptions() {

    Mockito.when(userDAOmock.getOneUserByEmail("test"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.login("test", "test");
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }


  @Test
  @DisplayName("Test findAll method with empty users list")
  public void testFindAllWithEmptyList() {

    List<UserDTO> emptyList = new ArrayList<>();
    Mockito.when(userDAOmock.getAllUsers()).thenReturn(emptyList);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.findAll();
    });
    Throwable cause = parentException.getCause();

    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test findAll method with only one user in list")
  public void testFindAllWithListHavingOneUser() {

    List<UserDTO> list = new ArrayList<>();
    UserDTO user = domainFactory.getUserDTO();
    list.add(user);

    Mockito.when(userDAOmock.getAllUsers()).thenReturn(list);
    List<UserDTO> receivedList = userUCC.findAll();

    assertNotNull(receivedList);
  }

  @Test
  @DisplayName("Test findAll method when UserDAO getAllUsers method throws FatalException")
  public void testFindAllExceptions() {

    Mockito.when(userDAOmock.getAllUsers())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.findAll();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test findOne method when UserDAO getOneUserById method throws FatalException")
  public void testFindOneExceptions() {

    Mockito.when(userDAOmock.getOneUserById(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.findOne(1);
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test updateRole method with no existing user")
  public void testUpdateRoleWithNoExistingUser() {

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.updateRole(0, "helper");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(ElementNotFoundException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).updateRole(0, "helper")
    );
  }


  @Test
  @DisplayName("Test updateRole method with a user having 'user' as role")
  public void testUpdateRoleWithUserHavingUserAsRole() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setRole("user");

    UserDTO updatedUser = domainFactory.getUserDTO();
    updatedUser.setId(1);
    updatedUser.setRole("helper");

    Mockito.when(userDAOmock.updateRole(1, "helper")).thenReturn(updatedUser);
    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(user);

    UserDTO finalUser = userUCC.updateRole(1, "helper");
    assertAll(
        () -> assertEquals(updatedUser, finalUser),
        () -> Mockito.verify(userDAOmock, Mockito.times(1)).updateRole(1, "helper")
    );
  }

  @Test
  @DisplayName("Test updateRole method with a user having a role other than 'user'")
  public void testUpdateRoleWithUserNotHavingUserAsRole() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(5);
    user.setRole("helper");

    Mockito.when(userDAOmock.getOneUserById(5)).thenReturn(user);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.updateRole(5, "helper");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(NotAllowedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).updateRole(5, "helper")
    );
  }

  @Test
  @DisplayName("Test updateRole method with a user having 'admin' as role")
  public void testUpdateRoleWithUserHavingAdminAsRole() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(5);
    user.setRole("admin");

    Mockito.when(userDAOmock.getOneUserById(5)).thenReturn(user);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.updateRole(5, "admin");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(NotAllowedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).updateRole(5, "admin")
    );
  }

  @Test
  @DisplayName("Test updateRole method when UserDAO editUser method returns null")
  public void testUpdateRoleWhenUserDAOUpdateRoleReturnsNull() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setRole("user");

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.updateRole(user.getId(), "helper")).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.updateRole(user.getId(), "helper");
    });

    Throwable cause = parentException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test updateRole method when UserDAO updateRole method throws FatalException")
  public void testUpdateRoleExceptions() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setRole("user");

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(user);
    Mockito.when(userDAOmock.updateRole(1, "helper"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.updateRole(1, "helper");
    });

    Mockito.verify(dalServicesMock, Mockito.times(1)).commit();
  }

  @Test
  @DisplayName("Test register method with new email")
  public void testRegisterWithUserEmailNotTaken() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setPassword("password");

    UserDTO registeredUser = domainFactory.getUserDTO();
    user.setId(1);
    UserDTO returnedUser;

    Mockito.when(userDAOmock.addOneUser(user)).thenReturn(registeredUser);
    returnedUser = userUCC.register(user);

    assertNotNull(returnedUser);
  }

  @Test
  @DisplayName("Test register method when UserDAO addOneUser method throws FatalException")
  public void testRegisterException() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail("test");

    Mockito.when(userDAOmock.addOneUser(user))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.register(user);
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test register method with the user's email already existed")
  public void testRegisterWithUserEmailAlreadyTaken() {

    String email = "test@gmail.com";

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(user);

    UserDTO userInDb = domainFactory.getUserDTO();
    userInDb.setId(2);
    userInDb.setEmail(email);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(userInDb);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.register(user);
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(NotAllowedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).addOneUser(user)
    );
  }

  @Test
  @DisplayName("Test register method when UserDAO addOneUser method returns null")
  public void testRegisterAddOneUserReturnsNull() {

    String email = "test@gmail.com";

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);

    Mockito.when(userDAOmock.addOneUser(user)).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.register(user);
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(UnauthorizedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.times(1)).addOneUser(user)
    );
  }

  @Test
  @DisplayName("Test editUser method when UserDAO getOneUserById method returns null")
  public void testEditUserGetOneUserByIdReturnsNull() {

    String email = "null@gmail.com";
    UserDTO user = domainFactory.getUserDTO();
    user.setId(0);
    user.setEmail(email);

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.editUser(user);
    });

    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(NotAllowedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.times(1)).getOneUserById(user.getId()),
        () -> Mockito.verify(userDAOmock, Mockito.never()).editUser(user),
        () -> Mockito.verify(userDAOmock, Mockito.never()).getOneUserByEmail(email)
    );
  }

  @Test
  @DisplayName("Test editUser method when new email already exists in database")
  public void testEditUserWithEmailAlreadyTaken() {

    String email = "test@gmail.com";
    UserDTO userToEdit = domainFactory.getUserDTO();
    userToEdit.setId(1);
    userToEdit.setEmail(email);

    UserDTO userFound = domainFactory.getUserDTO();
    userFound.setId(2);
    userFound.setEmail(email);

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(userToEdit);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(userFound);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.editUser(userToEdit);
    });

    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(NotAllowedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.times(1)).getOneUserById(userToEdit.getId()),
        () -> Mockito.verify(userDAOmock, Mockito.never()).editUser(userToEdit),
        () -> Mockito.verify(userDAOmock, Mockito.times(1)).getOneUserByEmail(email)
    );
  }

  @Test
  @DisplayName("Test editUser method when UserDAO editUser method returns null")
  public void testEditUserWhenUserDAOEditUserReturnsNull() {

    String email = "null@gmail.com";
    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(null);
    Mockito.when(userDAOmock.editUser(user)).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.editUser(user);
    });

    Throwable cause = parentException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test editUser method when UserDAO editUser method returns the edited user with "
      + "new email")
  public void testEditUserWhenUserDAOEditUserReturnsUserWithNewEmail() {

    String email = "test@gmail.com";
    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(null);
    Mockito.when(userDAOmock.editUser(user)).thenReturn(user);

    UserDTO editedUser = userUCC.editUser(user);

    assertNotNull(editedUser);
  }

  @Test
  @DisplayName("Test editUser method when UserDAO editUser method returns the edited user without "
      + "new email")
  public void testEditUserWhenUserDAOEditUserReturnsUserWithoutNewEmail() {

    String email = "test@gmail.com";
    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(user);
    Mockito.when(userDAOmock.editUser(user)).thenReturn(user);

    UserDTO editedUser = userUCC.editUser(user);

    assertNotNull(editedUser);
  }

  @Test
  @DisplayName("Test editUser method when user wants to edit its profile photo")
  public void testEditUserWithProfilePhotoToSet() {

    PhotoDTO profilePhoto = domainFactory.getPhotoDTO();
    profilePhoto.setId(1);

    String email = "test@gmail.com";
    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail(email);
    user.setProfilePhoto(profilePhoto);

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.getOneUserByEmail(email)).thenReturn(user);
    Mockito.when(userDAOmock.editUserWithPhoto(profilePhoto.getId(), user)).thenReturn(user);

    UserDTO editedUser = userUCC.editUser(user);

    assertAll(
        () -> assertNotNull(editedUser),
        () -> Mockito.verify(userDAOmock, Mockito.never()).editUser(user),
        () -> Mockito.verify(userDAOmock, Mockito.times(1))
            .editUserWithPhoto(profilePhoto.getId(), user)
    );
  }

  @Test
  @DisplayName("Test editUser method when UserDAO editUser method throws FatalException")
  public void testEditUserExceptions() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setEmail("null@gmail.com");

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(user);
    Mockito.when(userDAOmock.getOneUserByEmail("null@gmail.com")).thenReturn(null);
    Mockito.when(userDAOmock.editUser(user))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.editUser(user);
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test changePassword method when findOne method returns null")
  public void testChangePasswordWithNoExistingUser() {

    UserDTO userNotFound = domainFactory.getUserDTO();
    userNotFound.setId(0);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.changePassword(0, "oldPassword", "newPassword");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(ElementNotFoundException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).changePassword(userNotFound)
    );
  }

  @Test
  @DisplayName("Test changePassword method when oldPassword is incorrect")
  public void testChangePasswordWithIncorrectOldPassword() {

    UserDTO userFound = domainFactory.getUserDTO();
    userFound.setId(1);
    userFound.setPassword("mdp123");
    ((User) userFound).hashPassword();

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(userFound);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.changePassword(1, "oldPassword", "newPassword");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(UnauthorizedException.class, cause),
        () -> Mockito.verify(userDAOmock, Mockito.never()).changePassword(userFound)
    );
  }

  @Test
  @DisplayName("Test changePassword method when oldPassword is correct")
  public void testChangePasswordWithCorrectOldPassword() {

    UserDTO userFound = domainFactory.getUserDTO();
    userFound.setId(1);
    userFound.setPassword("oldPassword");
    ((User) userFound).hashPassword();

    UserDTO userNewPassword = domainFactory.getUserDTO();
    userNewPassword.setId(1);
    userNewPassword.setPassword("newPassword");
    ((User) userNewPassword).hashPassword();

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(userFound);
    Mockito.when(userDAOmock.changePassword(userFound)).thenReturn(userNewPassword);

    UserDTO userToChangePassword = userUCC.changePassword(1, "oldPassword", "newPassword");

    assertNotNull(userToChangePassword);
  }

  @Test
  @DisplayName("Test changePassword method when UserDAO changePassword method returns null")
  public void testChangePasswordWhenUserDAOChangePasswordReturnsNull() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setPassword("oldPassword");
    ((User) user).hashPassword();

    Mockito.when(userDAOmock.getOneUserById(1)).thenReturn(user);
    Mockito.when(userDAOmock.changePassword(user)).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      userUCC.changePassword(1, "oldPassword", "newPassword");
    });
    Throwable cause = parentException.getCause();

    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test changePassword method when UserDAO changePassword method throws "
      + "FatalException")
  public void testChangePasswordExceptions() {

    UserDTO user = domainFactory.getUserDTO();
    user.setId(1);
    user.setPassword("oldPassword");
    ((User) user).hashPassword();

    Mockito.when(userDAOmock.getOneUserById(user.getId())).thenReturn(user);
    Mockito.when(userDAOmock.changePassword(user))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      userUCC.changePassword(1, "oldPassword", "newPassword");
    });

    Mockito.verify(dalServicesMock, Mockito.times(1)).commit();
  }
}