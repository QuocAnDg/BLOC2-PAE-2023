package be.vinci.pae.domain;

import be.vinci.pae.services.PhotoDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of PhotoUCC.
 */
public class PhotoUCCImpl implements PhotoUCC {

  @Inject
  private PhotoDAO myPhotoDAO;
  @Inject
  private DALServices myDALServices;

  /**
   * Adds a photo.
   *
   * @param file     bytes containing the photo.
   * @param fileName the photo's name.
   * @param type     the type of photo.
   * @return the PhotoDTO object created.
   */
  @Override
  public PhotoDTO addPhoto(InputStream file, String fileName, String type) {
    UUID uuid = UUID.randomUUID();
    String extension = fileName.split("\\.")[1];
    fileName = uuid + "." + extension;

    String assetsFolder = Config.getProperty("AssetsFolderPath");

    try {
      if (!Files.exists(Paths.get(assetsFolder))) {
        Files.createDirectories(Paths.get(assetsFolder));
      }

      Files.copy(file, Paths.get(assetsFolder, fileName));
    } catch (IOException e) {
      throw new IllegalBusinessException(e);
    }
    PhotoDTO photoCreated;
    try {
      myDALServices.startTransaction();
      photoCreated = (Photo) myPhotoDAO.setPhoto(fileName, type);
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }
    if (photoCreated == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Photo not created in database")
      );
    }
    return photoCreated;
  }

  /**
   * Gets all photos with avatar type.
   *
   * @return a list containing photos with avatar type.
   */
  @Override
  public List<PhotoDTO> getAllAvatars() {

    List<PhotoDTO> listAvatars;
    try {
      myDALServices.startTransaction();
      listAvatars = myPhotoDAO.getAllAvatars();
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }

    return listAvatars;
  }

  /**
   * Adds a photo from an existing avatar.
   *
   * @param avatarPath the access path to the avatar.
   * @return the PhotoDTO object created.
   */
  @Override
  public PhotoDTO copyAvatar(String avatarPath) {

    String userDir = System.getProperty("user.dir");
    String relativePath = File.separator + "img" + File.separator + avatarPath;
    File source = new File(userDir + relativePath);

    UUID uuid = UUID.randomUUID();
    String extension = avatarPath.split("\\.")[1];
    avatarPath = uuid + "." + extension;

    String assetsFolder = Config.getProperty("AssetsFolderPath");

    try {
      if (!Files.exists(Paths.get(assetsFolder))) {
        Files.createDirectories(Paths.get(assetsFolder));
      }

      Files.copy(source.toPath(), Paths.get(assetsFolder, avatarPath));
    } catch (IOException e) {
      throw new IllegalBusinessException(e);
    }
    PhotoDTO photoCreated;
    try {
      myDALServices.startTransaction();
      photoCreated = (Photo) myPhotoDAO.setPhoto(avatarPath, "profile_picture");
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }
    if (photoCreated == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Photo based on avatar not created in database")
      );
    }
    return photoCreated;
  }
}
