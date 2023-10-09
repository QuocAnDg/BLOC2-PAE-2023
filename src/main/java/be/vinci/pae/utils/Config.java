package be.vinci.pae.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config class.
 */
public class Config {

  private static Properties props;

  /**
   * load configurations from a file.
   *
   * @param file the file to be loaded.
   */
  public static void load(String file) {
    props = new Properties();
    try (InputStream input = new FileInputStream(file)) {
      props.load(input);
    } catch (IOException e) {
      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain")
              .build());
    }
  }

  /**
   * Get a property of a key in the dev.properties file.
   *
   * @param key key that can represent all property keys in the dev.properties file.
   * @return the property as a String.
   */
  public static String getProperty(String key) {
    return props.getProperty(key);
  }

  /**
   * Get a property of a key in the dev.properties file.
   *
   * @param key key that can represent all property keys in the dev.properties file.
   * @return the property as a Integer.
   */
  public static Integer getIntProperty(String key) {
    return Integer.parseInt(props.getProperty(key));
  }

  /**
   * Get a property of a key in the dev.properties file.
   *
   * @param key key that can represent all property keys in the dev.properties file.
   * @return the property as a Boolean.
   */
  public static boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(props.getProperty(key));
  }

}
