package be.vinci.pae.services;

import be.vinci.pae.domain.AvailabilityDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * AvailabilityDAO interface.
 */
public interface AvailabilityDAO {

  /**
   * Get all availabilities dates in database.
   *
   * @return a list of LocalDate.
   */
  List<LocalDate> getAllAvailabilitiesDates();

  /**
   * Add an availability in database if it is not already present.
   *
   * @param date availability's date.
   * @return an AvailabilityDAO; null if an availability with the same date is already present.
   */
  AvailabilityDTO addAvailabilityDate(String date);

  /**
   * Get id of one availability date.
   *
   * @param date availability.date.
   * @return an id; -1 if not found.
   */
  int getIdOfAvailabilityDate(LocalDate date);
}
