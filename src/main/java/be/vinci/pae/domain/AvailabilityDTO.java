package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * AvailabilityDTO interface containing only getters and setters of an availability.
 */
public interface AvailabilityDTO {

  /**
   * Get id.
   *
   * @return the availability's date.
   */
  int getId();

  /**
   * Set id.
   *
   * @param id the availability's date.
   */
  void setId(int id);


  /**
   * Get date.
   *
   * @return the availability's date.
   */
  LocalDate getDate();

  /**
   * Set date.
   *
   * @param date the availability's date.
   */
  void setDate(LocalDate date);
}
