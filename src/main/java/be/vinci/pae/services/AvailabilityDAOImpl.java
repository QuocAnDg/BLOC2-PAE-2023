package be.vinci.pae.services;

import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AvailabilityDAO implementation.
 */
public class AvailabilityDAOImpl implements AvailabilityDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private DomainFactory domainFactory;

  /**
   * Add an availability in database if it is not already present.
   *
   * @param date availability's date.
   * @return an AvailabilityDAO; null if an availability with the same date is already present.
   */
  public AvailabilityDTO addAvailabilityDate(String date) {
    String sql = """
        INSERT INTO pae.availabilities VALUES(DEFAULT, ?) RETURNING *;
        """;
    try {
      PreparedStatement ps = dalBackendServices.getPreparedStatement(sql);
      LocalDate dateObject = LocalDate.parse(date);
      ps.setDate(1, Date.valueOf(dateObject));

      ResultSet rs = ps.executeQuery();
      if (!rs.next()) {
        return null;
      }
      AvailabilityDTO availability = domainFactory.getAvailabilityDTO();
      availability.setId(rs.getInt("availability_id"));
      availability.setDate(rs.getDate("availability_date").toLocalDate());

      return availability;


    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * get all availabilities dates in database.
   *
   * @return a list of LocalDate.
   */
  @Override
  public List<LocalDate> getAllAvailabilitiesDates() {
    String sql = """
            SELECT availability_id, availability_date
            FROM pae.availabilities
            WHERE availability_date > CURRENT_DATE
        """;
    List<LocalDate> availabilitiesDatesList = new ArrayList<>();
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          availabilitiesDatesList.add(LocalDate.parse(rs.getString("availability_date")));
        }
      }
      return availabilitiesDatesList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  @Override
  public int getIdOfAvailabilityDate(LocalDate date) {
    String sql = """
        SELECT availability_id, availability_date
        FROM pae.availabilities
        WHERE availability_date = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setDate(1, Date.valueOf(date));
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return -1;
        }
        return rs.getInt("availability_id");
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

}
