package be.vinci.pae.services;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PhotoDAO.
 */
public class PhotoDAOImpl implements PhotoDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private DomainFactory domainFactory;

  /**
   * Adds the access path of a photo in database.
   *
   * @return the photoDTO associated with the photo object created in database or null.
   */
  @Override
  public PhotoDTO setPhoto(String accessPath, String type) {
    String sql = "INSERT INTO pae.photos VALUES (DEFAULT, ?, ?) RETURNING *";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, accessPath);
      ps.setString(2, type);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        PhotoDTO photoCreated = domainFactory.getPhotoDTO();
        setPhotoDAOAttributes(rs, photoCreated);
        return photoCreated;

      } catch (SQLException e) {
        throw new FatalException(e);
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * Get all photos with 'avatar' as type in the database.
   *
   * @return a list containing photos with 'avatar' as type.
   */
  @Override
  public List<PhotoDTO> getAllAvatars() {

    List<PhotoDTO> listAvatars = new ArrayList<>();

    String sql = """
        SELECT p.photo_id, p.access_path, p.type
        FROM pae.photos p
        WHERE p.type = 'avatar'
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
          PhotoDTO avatar = domainFactory.getPhotoDTO();
          setPhotoDAOAttributes(rs, avatar);
          listAvatars.add(avatar);
        }
      }
      return listAvatars;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Set photoDAO attributes.
   *
   * @param rs    the current row of the ResultSet.
   * @param photo the photoDTO to set.
   */
  private void setPhotoDAOAttributes(ResultSet rs, PhotoDTO photo) {
    try {
      photo.setId(rs.getInt("photo_id"));
      photo.setAccessPath(rs.getString("access_path"));
      photo.setType(rs.getString("type"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
