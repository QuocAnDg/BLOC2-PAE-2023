package be.vinci.pae.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger class.
 */
public class MyLogger {

  private static final int FILE_SIZE_LIMIT = 1000000; // 1MB
  private static final String LOG_FILE_NAME = "logging.log";

  private static MyLogger myLogger = new MyLogger();
  private Logger logger;

  /**
   * MyLogger singleton to log all events.
   */
  private MyLogger() {
    logger = Logger.getLogger("My Logger");
    FileHandler handler;

    try {
      handler = new FileHandler(LOG_FILE_NAME, FILE_SIZE_LIMIT, 1, true);
      logger.addHandler(handler);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error creating log file", e);
    }
  }

  /**
   * Instance of MyLogger.
   *
   * @return MyLogger class
   */
  public static MyLogger getInstance() {
    return myLogger;
  }

  /**
   * Get My logger.
   *
   * @return Logger
   */
  public Logger getLogger() {
    return logger;
  }


}
