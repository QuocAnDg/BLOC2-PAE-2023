package be.vinci.pae.services;

import be.vinci.pae.domain.PhotoDTO;
import java.util.List;

/**
 * PhotoDAO interface.
 */
public interface PhotoDAO {

  /**
   * Get in the database all the items as well as the user who proposed the item.
   *
   * @param accessPath the access path to the photo.
   * @param type       the type of photo.
   * @return a list of itemDTO; null if no item in the database.
   */
  PhotoDTO setPhoto(String accessPath, String type);

  /**
   * Get all photos with 'avatar' as type in the database.
   *
   * @return a list containing photos with 'avatar' as type.
   */
  List<PhotoDTO> getAllAvatars();
}
