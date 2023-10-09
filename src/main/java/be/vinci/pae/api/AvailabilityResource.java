package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.Logged;
import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.AvailabilityUCC;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDate;
import java.util.List;


/**
 * AvailabilityResource class.
 */
@Singleton
@Path("/availability")
public class AvailabilityResource {

  @Inject
  private AvailabilityUCC myAvailabilityUCC;

  /**
   * Get all items resource path.
   *
   * @return a list of all items as a JSON
   */
  @GET
  @Path("/all")
  @Logged
  @Produces(MediaType.APPLICATION_JSON)
  public List<LocalDate> getAllAvailabilitiesDates() {
    List<LocalDate> availabilitiesDatesList;
    try {
      availabilitiesDatesList = myAvailabilityUCC.getAllAvailabilitiesDates();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return availabilitiesDatesList;
  }

  /**
   * Add availability date resource path.
   *
   * @param json json object.
   * @return a list of all items as a JSON
   */
  @POST
  @Path("/add")
  @Logged
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"helper", "admin"})
  public AvailabilityDTO addAvailabilityDate(JsonNode json) {
    AvailabilityDTO availabilityCreated;
    if (!json.hasNonNull("date")) {
      throw new WebApplicationException("Date required", Status.BAD_REQUEST);
    }
    String date = json.get("date").asText();
    try {
      availabilityCreated = myAvailabilityUCC.addAvailability(date);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return availabilityCreated;
  }
}
