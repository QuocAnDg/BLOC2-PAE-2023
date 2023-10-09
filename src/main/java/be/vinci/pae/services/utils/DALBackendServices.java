package be.vinci.pae.services.utils;

import java.sql.PreparedStatement;

/**
 * DALServices inferface.
 */
public interface DALBackendServices {

  /**
   * Get a prepared statement.
   *
   * @param sql a sql request.
   * @return a prepared statement.
   */
  PreparedStatement getPreparedStatement(String sql);
}
