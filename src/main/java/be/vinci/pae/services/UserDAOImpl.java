package be.vinci.pae.services;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO class.
 */
public class UserDAOImpl implements UserDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private DomainFactory domainFactory;


  /**
   * Find in the database a user associated with a given email.
   *
   * @param email the user's email.
   * @return the user associated with the address email if found; null else.
   */
  @Override
  public UserDTO getOneUserByEmail(String email) {
    String sql = """
        SELECT u.version, u.user_id, u.last_name, u.first_name, u.email, u.password, u.phone_number,
         u.role,
        u.registration_date, p.photo_id, p.access_path, p.type
        FROM pae.users u, pae.photos p
        WHERE u.email = ? AND u.profile_picture = p.photo_id
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, email);
      return getUserDTOFromPreparedStatement(ps);

    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Find in the database a user associated with a given id and its profile photo.
   *
   * @param id the user's id.
   * @return the user associated with the id if found; null else.
   */
  @Override
  public UserDTO getOneUserById(int id) {
    String sql = """
        SELECT u.version, u.user_id, u.last_name, u.first_name, u.email, u.password,
        u.phone_number, u.role, u.registration_date, p.photo_id, p.access_path, p.type
        FROM pae.users u, pae.photos p
        WHERE u.user_id = ? AND u.profile_picture = p.photo_id
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, id);
      return getUserDTOFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Add one user.
   *
   * @param user the user to be added.
   * @return the user added.
   */
  @Override
  public UserDTO addOneUser(UserDTO user) {
    LocalDate now = LocalDate.now();
    String sql = """
        INSERT INTO pae.users VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, 'user', ?)
        RETURNING user_id
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, user.getLastname());
      ps.setString(2, user.getFirstname());
      ps.setString(3, user.getEmail());
      ps.setString(4, user.getPassword());
      ps.setString(5, user.getPhoneNumber());
      ps.setInt(6, user.getProfilePhoto().getId());
      ps.setDate(7, Date.valueOf(now));

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return getOneUserById(rs.getInt("user_id"));
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Get all users from the database who don't have the role admin.
   *
   * @return A list containing all users.
   */
  @Override
  public List<UserDTO> getAllUsers() {

    List<UserDTO> usersList = new ArrayList<>();
    String sql = """
        SELECT u.version, u.user_id, u.last_name, u.first_name, u.email, u.password,
        u.phone_number, u.role, u.registration_date, p.photo_id, p.access_path, p.type
        FROM pae.users u, pae.photos p
        WHERE u.profile_picture = p.photo_id
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          UserDTO userDTO = domainFactory.getUserDTO();
          setUserDTOAttributes(rs, userDTO);
          usersList.add(userDTO);
        }
      }
      return usersList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all helpers and admin.
   *
   * @return a list containing all the helpers and admin.
   */
  @Override
  public List<UserDTO> getAllHelpersAndAdmin() {

    List<UserDTO> usersList = new ArrayList<>();
    String sql = """
        SELECT u.user_id, u.last_name, u.first_name, u.email, u.password, u.phone_number, u.role,
        u.registration_date, p.photo_id, p.access_path, p.type, u.version
        FROM pae.users u, pae.photos p
        WHERE (u.role = 'admin' OR u.role = 'helper') AND u.profile_picture = p.photo_id
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          UserDTO userDTO = domainFactory.getUserDTO();
          setUserDTOAttributes(rs, userDTO);
          usersList.add(userDTO);
        }
      }
      return usersList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Find in the database a user associated with a given id then, update the role of a user from
   * "user" to "helper" or "admin".
   *
   * @param id      the user's id.
   * @param newRole the user's new role.
   * @return the User with the updated role.
   */
  @Override
  public UserDTO updateRole(int id, String newRole) {

    String sql = """
        UPDATE pae.users
        SET role = ?
        WHERE user_id = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, newRole);
      ps.setInt(2, id);
      ps.executeUpdate();
      return getOneUserById(id);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Edit user's firstname, lastname, email and phone number.
   *
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  @Override
  public UserDTO editUser(UserDTO userToEdit) {

    String sql = """
        UPDATE pae.users
        SET first_name = ?, last_name = ?, email = ?, phone_number = ?, version = version + 1
        WHERE user_id = ? AND version = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, userToEdit.getFirstname());
      ps.setString(2, userToEdit.getLastname());
      ps.setString(3, userToEdit.getEmail());
      ps.setString(4, userToEdit.getPhoneNumber());
      ps.setInt(5, userToEdit.getId());
      ps.setInt(6, userToEdit.getVersion());

      int rowsModified = ps.executeUpdate();
      if (rowsModified == 0) {
        throw new IllegalBusinessException(
            new ConflictException("User version in database doesn't match user version received"));
      }

      return getOneUserById(userToEdit.getId());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Change the user's password.
   *
   * @param userDTO the user to change its password.
   * @return the UserDTO with the new password.
   */
  @Override
  public UserDTO changePassword(UserDTO userDTO) {

    String sql = """
        UPDATE pae.users
        SET password = ?
        WHERE user_id = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, userDTO.getPassword());
      ps.setInt(2, userDTO.getId());
      ps.executeUpdate();
      return getOneUserById(userDTO.getId());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Edit user's firstname, lastname, email, phone number and photo.
   *
   * @param userToEdit the user to edit.
   * @return the edited user.
   */
  @Override
  public UserDTO editUserWithPhoto(int idPhoto, UserDTO userToEdit) {

    String sql = """
        UPDATE pae.users
        SET first_name = ?, last_name = ?, email = ?, phone_number = ?, profile_picture = ?,
        version = version + 1
        WHERE user_id = ? AND version = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, userToEdit.getFirstname());
      ps.setString(2, userToEdit.getLastname());
      ps.setString(3, userToEdit.getEmail());
      ps.setString(4, userToEdit.getPhoneNumber());
      ps.setInt(5, idPhoto);
      ps.setInt(6, userToEdit.getId());
      ps.setInt(7, userToEdit.getVersion());

      int rowsModified = ps.executeUpdate();
      if (rowsModified == 0) {
        throw new IllegalBusinessException(
            new ConflictException("User version in database doesn't match user version received"));
      }

      return getOneUserById(userToEdit.getId());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * get UserDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a userDTO containing all information about the existing user; null if no user found;
   */
  private UserDTO getUserDTOFromPreparedStatement(PreparedStatement ps) {
    UserDTO userDTO = domainFactory.getUserDTO();
    try (ResultSet rs = ps.executeQuery()) {
      if (!rs.next()) {
        return null;
      }
      do {
        setUserDTOAttributes(rs, userDTO);
      } while (rs.next());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return userDTO;
  }

  /**
   * Set userDTO attributes.
   *
   * @param rs      the current row of the ResultSet.
   * @param userDTO the userDTO to set.
   */
  private void setUserDTOAttributes(ResultSet rs, UserDTO userDTO) {
    try {
      PhotoDTO userProfilePhoto = domainFactory.getPhotoDTO();
      userProfilePhoto.setId(rs.getInt("photo_id"));
      userProfilePhoto.setAccessPath(rs.getString("access_path"));
      userProfilePhoto.setType(rs.getString("type"));

      userDTO.setId(rs.getInt("user_id"));
      userDTO.setLastname(rs.getString("last_name"));
      userDTO.setFirstname(rs.getString("first_name"));
      userDTO.setEmail(rs.getString("email"));
      userDTO.setPassword(rs.getString("password"));
      userDTO.setProfilePhoto(userProfilePhoto);
      userDTO.setPhoneNumber(rs.getString("phone_number"));
      userDTO.setRole(rs.getString("role"));
      userDTO.setRegisterDate(LocalDate.parse(rs.getString("registration_date")));
      userDTO.setVersion(rs.getInt("version"));
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}


