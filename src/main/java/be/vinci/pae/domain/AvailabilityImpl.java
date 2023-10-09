package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * AvailabilityDTO implementation of the AvailabilityDTO interface.
 */
class AvailabilityImpl implements AvailabilityDTO {

  private int id;
  private LocalDate date;

  public AvailabilityImpl() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }


}
