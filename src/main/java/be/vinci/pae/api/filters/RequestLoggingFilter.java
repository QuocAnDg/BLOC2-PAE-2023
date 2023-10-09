package be.vinci.pae.api.filters;

import be.vinci.pae.utils.MyLogger;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;

/**
 * RequestLoggingFilter class. Logs every path marked with the @Logged annotation.
 */
@Logged
@Provider
public class RequestLoggingFilter implements ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) {
    MyLogger.getInstance().getLogger().log(Level.INFO, requestContext.getMethod() + " request to "
        + requestContext.getUriInfo().getPath()
        + " - Response : " + responseContext.getStatus());
  }
}