package be.vinci.pae.domain;


import java.time.LocalDate;
import java.util.List;

/**
 * AvailabilityUCC.
 */
public interface AvailabilityUCC {

  /**
   * Get all availabilities dates.
   *
   * @return a list of LocalDate.
   */
  List<LocalDate> getAllAvailabilitiesDates();

  /**
   * Adds an availability.
   *
   * @param date the availability's date.
   * @return the AvailabilityDTO object created.
   */
  AvailabilityDTO addAvailability(String date);

  /**
   * Get id of one availability date.
   *
   * @param date availability.date.
   * @return an id; -1 if not found.
   */
  int getIdOfOneAvailabilityDate(LocalDate date);
}
