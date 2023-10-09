package be.vinci.pae.domain;

import java.io.InputStream;
import java.util.List;

/**
 * PhotoUCC.
 */
public interface PhotoUCC {

  /**
   * Adds a photo.
   *
   * @param file     bytes containing the photo.
   * @param fileName the photo's name.
   * @param type     the type of photo.
   * @return the PhotoDTO object created.
   */
  PhotoDTO addPhoto(InputStream file, String fileName, String type);

  /**
   * Gets all photos with avatar type.
   *
   * @return a list containing photos with avatar type.
   */
  List<PhotoDTO> getAllAvatars();

  /**
   * Adds a photo from an existing avatar.
   *
   * @param avatarPath the access path to the avatar.
   * @return the PhotoDTO object created.
   */
  PhotoDTO copyAvatar(String avatarPath);
}
