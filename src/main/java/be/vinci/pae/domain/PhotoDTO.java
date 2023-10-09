package be.vinci.pae.domain;


/**
 * PhotoDTO interface containing only getters and setters of a Photo.
 */
public interface PhotoDTO {

  /**
   * Getter for id.
   *
   * @return the photo's id.
   */
  int getId();

  /**
   * Setter for id.
   *
   * @param id the photo's id to set.
   */
  void setId(int id);

  /**
   * Getter for accessPath.
   *
   * @return the access path of the photo.
   */
  String getAccessPath();

  /**
   * Setter for accessPath.
   *
   * @param accessPath set the access path of the photo.
   */
  void setAccessPath(String accessPath);

  /**
   * Getter for type.
   *
   * @return the type of the photo.
   */
  String getType();

  /**
   * Setter for type.
   *
   * @param type the type of the photo to set.
   */
  void setType(String type);

}
