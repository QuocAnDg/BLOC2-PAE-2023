package be.vinci.pae.api.filters;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Authorize annotation interface.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {

  /**
   * Authorize parameter containing the expected role(s) for a method having the Authorize
   * annotation.
   *
   * @return the authorized role(s).
   */
  String[] roles() default {};
}
