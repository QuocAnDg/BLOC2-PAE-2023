package be.vinci.pae.api;

import be.vinci.pae.api.filters.Logged;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.PhotoUCC;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * PhotoResource class.
 */
@Singleton
@Path("/photo")
public class PhotoResource {

  @Inject
  private PhotoUCC myPhotoUCC;


  /**
   * Get avatars route.
   *
   * @return a list containing photos with 'avatar' as type.
   */
  @GET
  @Logged
  @Path("avatars")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PhotoDTO> getAllAvatars() {
    List<PhotoDTO> listAvatars;

    try {
      listAvatars = myPhotoUCC.getAllAvatars();
    } catch (FatalException e) {
      throw e;
    }
    return listAvatars;
  }
}
