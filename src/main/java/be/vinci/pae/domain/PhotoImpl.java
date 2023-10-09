package be.vinci.pae.domain;

/**
 * Implementation of the Photo interface.
 */
class PhotoImpl implements Photo {

  private int id;
  private String accessPath;
  private String type;


  public PhotoImpl() {

  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getAccessPath() {
    return accessPath;
  }

  @Override
  public void setAccessPath(String accessPath) {
    this.accessPath = accessPath;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

}