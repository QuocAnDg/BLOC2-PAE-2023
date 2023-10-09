package be.vinci.pae.services.utils;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InternalServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;


/**
 * DALServices class.
 */
public class DALServicesImpl implements DALBackendServices, DALServices {

  private static final ThreadLocal<Connection> mapConnections = new ThreadLocal<>();
  private BasicDataSource dataSource = new BasicDataSource();

  /**
   * Creates a connection to the database.
   */
  public DALServicesImpl() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.out.println("Driver PostgreSQL manquant !");
      System.exit(1);
    }
    dataSource.setUrl(Config.getProperty("DBPath"));
    dataSource.setUsername(Config.getProperty("DBUser"));
    dataSource.setPassword(Config.getProperty("DBPassword"));
    dataSource.setMinIdle(5);
    dataSource.setMaxIdle(10);
    dataSource.setMaxOpenPreparedStatements(100);
  }

  /**
   * Get a prepared statement.
   *
   * @param sql a sql request.
   * @return a prepared statement.
   */
  @Override
  public PreparedStatement getPreparedStatement(String sql) {
    try {
      Connection connection = mapConnections.get();
      if (connection == null) {
        throw new InternalServerException("No connection in local thread");
      }
      return connection.prepareStatement(sql);
    } catch (SQLException e) {
      String error = "Erreur avec les requêtes SQL !";
      System.out.println(error);
      throw new FatalException(e);
    }
  }

  /**
   * start a transaction.
   */
  @Override
  public void startTransaction() {
    try {
      Connection connection = dataSource.getConnection();
      mapConnections.set(connection);
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * commit changes to database.
   */
  @Override
  public void commit() {
    Connection connection = mapConnections.get();
    if (mapConnections.get() == null) {
      throw new InternalServerException("No connection in local thread");
    }
    try {
      connection.commit();
    } catch (SQLException e) {
      throw new FatalException(e);
    } finally {
      mapConnections.remove();
      try {
        connection.close();
      } catch (SQLException e) {
        throw new FatalException(e);
      }
    }
  }

  /**
   * rollback changes to database.
   */
  @Override
  public void rollback() {
    Connection connection = mapConnections.get();
    if (connection == null) {
      throw new InternalServerException("No connection in local thread");
    }
    try {
      connection.rollback();
    } catch (SQLException e) {
      throw new FatalException(e);
    } finally {
      mapConnections.remove();
      try {
        connection.close();
      } catch (SQLException e) {
        throw new FatalException(e);
      }
    }

  }
}
