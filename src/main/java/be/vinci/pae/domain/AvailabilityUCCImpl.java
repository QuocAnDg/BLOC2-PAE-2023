package be.vinci.pae.domain;

import be.vinci.pae.services.AvailabilityDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of AvailabilityUCC.
 */
public class AvailabilityUCCImpl implements AvailabilityUCC {

  @Inject
  private AvailabilityDAO myAvailabilityDAO;
  @Inject
  private DALServices myDALServices;

  /**
   * Get all availabilities dates.
   *
   * @return a list of LocalDate.
   */
  @Override
  public List<LocalDate> getAllAvailabilitiesDates() {
    List<LocalDate> availabilitiesDates;
    try {
      myDALServices.startTransaction();
      availabilitiesDates = myAvailabilityDAO.getAllAvailabilitiesDates();
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }
    if (availabilitiesDates.isEmpty()) {
      throw new IllegalBusinessException(new NoContentException("Availabilities dates not found"));
    }
    return availabilitiesDates;
  }

  /**
   * Adds an availability.
   *
   * @param date the availability's date.
   * @return the AvailabilityDTO object created.
   */
  @Override
  public AvailabilityDTO addAvailability(String date) {

    AvailabilityDTO availabilityCreated;
    try {
      myDALServices.startTransaction();
      availabilityCreated = myAvailabilityDAO.addAvailabilityDate(date);
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }
    if (availabilityCreated == null) {
      throw new IllegalBusinessException(
          new UnauthorizedException("Availability not created in database")
      );
    }
    return availabilityCreated;
  }

  /**
   * Get id of one availability date.
   *
   * @param date availability.date.
   * @return an id; -1 if not found.
   */
  public int getIdOfOneAvailabilityDate(LocalDate date) {
    int availabilityDateId;
    try {
      myDALServices.startTransaction();
      availabilityDateId = myAvailabilityDAO.getIdOfAvailabilityDate(date);
      if (availabilityDateId == -1) {
        throw new IllegalBusinessException(
                new ElementNotFoundException("Date not found.")
        );
      }
      myDALServices.commit();
    } catch (Exception e) {
      myDALServices.rollback();
      throw e;
    }
    return availabilityDateId;
  }

}
