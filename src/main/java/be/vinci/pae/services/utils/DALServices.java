package be.vinci.pae.services.utils;

/**
 * DALServices interface.
 */
public interface DALServices {

  /**
   * start a transaction.
   */
  void startTransaction();

  /**
   * commit changes to database.
   */
  void commit();

  /**
   * rollback changes to database.
   */
  void rollback();
}
